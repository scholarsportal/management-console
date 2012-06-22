/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.hybrid;

import org.duracloud.account.db.BaseRepo;
import org.duracloud.account.db.DuracloudAccountClusterRepo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudGroupRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudServerDetailsRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudServiceRepositoryRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.amazonsimple.AmazonSimpleDBClientMgr;
import org.duracloud.account.db.amazonsimple.DuracloudAccountClusterRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudAccountRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudComputeProviderAccountRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudInstanceRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudServerDetailsRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudServerImageRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudServiceRepositoryRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudStorageProviderAccountRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudUserInvitationRepoImpl;
import org.duracloud.account.db.error.DBException;
import org.duracloud.account.db.error.DBUninitializedException;
import org.duracloud.account.db.ldap.DuracloudGroupRepoImpl;
import org.duracloud.account.db.ldap.DuracloudRightsRepoImpl;
import org.duracloud.account.db.ldap.DuracloudUserRepoImpl;
import org.duracloud.account.init.domain.AmaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.HashSet;
import java.util.Set;

/**
 * This class serves both SimpleDB and LDAP repos.
 *
 * @author Andrew Woods
 *         Date: Jun 20, 2012
 */
public class HybridDBRepoMgr implements DuracloudRepoMgr {

    private final Logger log = LoggerFactory.getLogger(HybridDBRepoMgr.class);

    private DuracloudUserRepo userRepo;
    private DuracloudGroupRepo groupRepo;
    private DuracloudAccountRepo accountRepo;
    private DuracloudRightsRepo rightsRepo;
    private DuracloudUserInvitationRepo userInvitationRepo;
    private DuracloudInstanceRepo instanceRepo;
    private DuracloudServerImageRepo serverImageRepo;
    private DuracloudComputeProviderAccountRepo computeProviderAccountRepo;
    private DuracloudStorageProviderAccountRepo storageProviderAccountRepo;
    private DuracloudServiceRepositoryRepo serviceRepositoryRepo;
    private DuracloudServerDetailsRepo serverDetailsRepo;
    private DuracloudAccountClusterRepo accountClusterRepo;

    private IdUtil idUtil;

    private final String DOMAIN_PREFIX;

    private AmazonSimpleDBClientMgr dbClientMgr;
    private LdapTemplate ldapTemplate;

    public HybridDBRepoMgr(IdUtil idUtil) {
        this(idUtil, null);
    }

    public HybridDBRepoMgr(IdUtil idUtil, String prefix) {
        this.idUtil = idUtil;
        this.DOMAIN_PREFIX = prefix;
    }

    @Override
    public void initialize(AmaConfig config) throws DBException {
        log.debug("initializing");

        AmazonSimpleDBClientMgr dbClientMgr = createSimpleDBClientMgr(config);
        LdapTemplate ldapTemplate = createLdapTemplate(config);

        // LDAP repos
        userRepo = new DuracloudUserRepoImpl(ldapTemplate);
        groupRepo = new DuracloudGroupRepoImpl(ldapTemplate);
        rightsRepo = new DuracloudRightsRepoImpl(ldapTemplate);

        if (null != DOMAIN_PREFIX) {
            String acctTable = DOMAIN_PREFIX + "_ACCT_DOMAIN";
            String userInvitationTable =
                DOMAIN_PREFIX + "_USER_INVITATION_DOMAIN";
            String instanceTable = DOMAIN_PREFIX + "_INSTANCE_DOMAIN";
            String serverImageTable = DOMAIN_PREFIX + "_SERVER_IMAGE_DOMAIN";
            String computeProviderAccountTable =
                DOMAIN_PREFIX + "_COMPUTE_PROVIDER_ACCOUNT_DOMAIN";
            String storageProviderAccountTable =
                DOMAIN_PREFIX + "_STORAGE_PROVIDER_ACCOUNT_DOMAIN";
            String serviceRepositoryTable =
                DOMAIN_PREFIX + "_SERVICE_REPOSITORY_DOMAIN";
            String serverDetailsTable =
                DOMAIN_PREFIX + "_SERVER_DETAILS_DOMAIN";
            String accountClusterTable =
                DOMAIN_PREFIX + "_ACCOUNT_CLUSTER_DOMAIN";

            // Amazon repos
            accountRepo = new DuracloudAccountRepoImpl(dbClientMgr, acctTable);
            userInvitationRepo =
                new DuracloudUserInvitationRepoImpl(dbClientMgr,
                                                    userInvitationTable);
            instanceRepo = new DuracloudInstanceRepoImpl(dbClientMgr,
                                                         instanceTable);
            serverImageRepo = new DuracloudServerImageRepoImpl(dbClientMgr,
                                                               serverImageTable);
            computeProviderAccountRepo =
                new DuracloudComputeProviderAccountRepoImpl(dbClientMgr,
                                                            computeProviderAccountTable);
            storageProviderAccountRepo =
                new DuracloudStorageProviderAccountRepoImpl(dbClientMgr,
                                                            storageProviderAccountTable);
            serviceRepositoryRepo = new DuracloudServiceRepositoryRepoImpl(
                dbClientMgr,
                serviceRepositoryTable);
            serverDetailsRepo = new DuracloudServerDetailsRepoImpl(dbClientMgr,
                                                                   serverDetailsTable);
            accountClusterRepo =
                new DuracloudAccountClusterRepoImpl(dbClientMgr,
                                                    accountClusterTable);

        } else {
            // Amazon repos
            accountRepo = new DuracloudAccountRepoImpl(dbClientMgr);
            userInvitationRepo =
                new DuracloudUserInvitationRepoImpl(dbClientMgr);
            instanceRepo = new DuracloudInstanceRepoImpl(dbClientMgr);
            serverImageRepo = new DuracloudServerImageRepoImpl(dbClientMgr);
            computeProviderAccountRepo =
                new DuracloudComputeProviderAccountRepoImpl(dbClientMgr);
            storageProviderAccountRepo =
                new DuracloudStorageProviderAccountRepoImpl(dbClientMgr);
            serviceRepositoryRepo = new DuracloudServiceRepositoryRepoImpl(
                dbClientMgr);
            serverDetailsRepo = new DuracloudServerDetailsRepoImpl(dbClientMgr);
            accountClusterRepo =
                new DuracloudAccountClusterRepoImpl(dbClientMgr);
        }

        idUtil.initialize(userRepo,
                          groupRepo,
                          accountRepo,
                          rightsRepo,
                          userInvitationRepo,
                          instanceRepo,
                          serverImageRepo,
                          computeProviderAccountRepo,
                          storageProviderAccountRepo,
                          serviceRepositoryRepo,
                          serverDetailsRepo,
                          accountClusterRepo);
    }

    private AmazonSimpleDBClientMgr createSimpleDBClientMgr(AmaConfig config) {
        if (null != dbClientMgr) {
            return dbClientMgr;
        }
        return new AmazonSimpleDBClientMgr(config.getUsername(),
                                           config.getPassword());
    }

    private LdapTemplate createLdapTemplate(AmaConfig config) {
        if (null != ldapTemplate) {
            return ldapTemplate;
        }

        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(config.getLdapUrl());
        contextSource.setBase(config.getLdapBaseDn());
        contextSource.setUserDn(config.getLdapUserDn());
        contextSource.setPassword(config.getLdapPassword());
        try {
            contextSource.afterPropertiesSet();

        } catch (Exception e) {
            log.error("Error creating LdapContentSource", e);
            throw new DBException("Error creating LdapContentSource", e);
        }

        return new LdapTemplate(contextSource);
    }

    @Override
    public DuracloudUserRepo getUserRepo() {
        checkInitialized(this.userRepo, "DuracloudUserRepo");
        return this.userRepo;
    }

    @Override
    public DuracloudGroupRepo getGroupRepo() {
        checkInitialized(this.groupRepo, "DuracloudGroupRepo");
        return this.groupRepo;
    }

    @Override
    public DuracloudAccountRepo getAccountRepo() {
        checkInitialized(this.accountRepo, "DuracloudAccountRepo");
        return this.accountRepo;
    }

    @Override
    public DuracloudRightsRepo getRightsRepo() {
        checkInitialized(this.rightsRepo, "DuracloudRightsRepo");
        return this.rightsRepo;
    }

    @Override
    public DuracloudInstanceRepo getInstanceRepo() {
        checkInitialized(this.instanceRepo, "DuracloudInstanceRepo");
        return this.instanceRepo;
    }

    @Override
    public DuracloudServerImageRepo getServerImageRepo() {
        checkInitialized(this.serverImageRepo, "DuracloudServerImageRepo");
        return this.serverImageRepo;
    }

    @Override
    public DuracloudUserInvitationRepo getUserInvitationRepo() {
        checkInitialized(this.userInvitationRepo,
                         "DuracloudUserInvitationRepo");
        return this.userInvitationRepo;
    }

    @Override
    public DuracloudComputeProviderAccountRepo getComputeProviderAccountRepo() {
        checkInitialized(this.computeProviderAccountRepo,
                         "DuracloudComputeProviderAccountRepo");
        return this.computeProviderAccountRepo;
    }

    @Override
    public DuracloudStorageProviderAccountRepo getStorageProviderAccountRepo() {
        checkInitialized(this.storageProviderAccountRepo,
                         "DuracloudStorageProviderAccountRepo");
        return this.storageProviderAccountRepo;
    }

    @Override
    public DuracloudServiceRepositoryRepo getServiceRepositoryRepo() {
        checkInitialized(this.serviceRepositoryRepo,
                         "DuracloudServiceRepositoryRepo");
        return this.serviceRepositoryRepo;
    }

    @Override
    public DuracloudServerDetailsRepo getServerDetailsRepo() {
        checkInitialized(this.serverDetailsRepo, "DuracloudServerDetailsRepo");
        return this.serverDetailsRepo;
    }

    @Override
    public DuracloudAccountClusterRepo getAccountClusterRepo() {
        checkInitialized(this.accountClusterRepo,
                         "DuracloudAccountClusterRepo");
        return this.accountClusterRepo;
    }

    @Override
    public IdUtil getIdUtil() {
        checkInitialized(this.idUtil, "IdUtil");
        return this.idUtil;
    }

    @Override
    public Set<BaseRepo> getAllRepos() {
        Set<BaseRepo> repos = new HashSet<BaseRepo>();
        repos.add(getUserRepo());
        repos.add(getGroupRepo());
        repos.add(getAccountRepo());
        repos.add(getRightsRepo());
        repos.add(getUserInvitationRepo());
        repos.add(getInstanceRepo());
        repos.add(getServerImageRepo());
        repos.add(getComputeProviderAccountRepo());
        repos.add(getStorageProviderAccountRepo());
        repos.add(getServiceRepositoryRepo());
        repos.add(getServerDetailsRepo());
        repos.add(getAccountClusterRepo());
        return repos;
    }

    private void checkInitialized(Object member, String name) {
        if (null == member) {
            String msg = name + " is not initialized";
            log.error(msg);
            throw new DBUninitializedException(msg);
        }
    }

    protected void setDbClientMgr(AmazonSimpleDBClientMgr dbClientMgr) {
        this.dbClientMgr = dbClientMgr;
    }

    protected void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }
}
