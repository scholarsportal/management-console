/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.db.BaseRepo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudProviderAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudServiceRepositoryRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBException;
import org.duracloud.account.db.error.DBUninitializedException;
import org.duracloud.account.init.domain.AmaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Dec 9, 2010
 */
public class AmazonSimpleDBRepoMgr implements DuracloudRepoMgr {

    private final Logger log =
        LoggerFactory.getLogger(AmazonSimpleDBRepoMgr.class);

    private DuracloudUserRepo userRepo;
    private DuracloudAccountRepo accountRepo;
    private DuracloudRightsRepo rightsRepo;
    private DuracloudUserInvitationRepo userInvitationRepo;
    private DuracloudInstanceRepo instanceRepo;
    private DuracloudServerImageRepo serverImageRepo;
    private DuracloudProviderAccountRepo providerAccountRepo;
    private DuracloudServiceRepositoryRepo serviceRepositoryRepo;

    private IdUtil idUtil;

    private final String DOMAIN_PREFIX;

    public AmazonSimpleDBRepoMgr(IdUtil idUtil) {
        this(idUtil, null);
    }

    public AmazonSimpleDBRepoMgr(IdUtil idUtil, String prefix) {
        this.idUtil = idUtil;
        this.DOMAIN_PREFIX = prefix;
    }

    @Override
    public void initialize(AmaConfig config) throws DBException {
        log.debug("initializing");

        AmazonSimpleDBClientMgr dbClientMgr =
            new AmazonSimpleDBClientMgr(config.getUsername(),
                                        config.getPassword());

        if (null != DOMAIN_PREFIX) {
            String userTable = DOMAIN_PREFIX + "_USER_DOMAIN";
            String acctTable = DOMAIN_PREFIX + "_ACCT_DOMAIN";
            String rightsTable = DOMAIN_PREFIX + "_RIGHTS_DOMAIN";
            String userInvitationTable =
                DOMAIN_PREFIX + "_USER_INVITATION_DOMAIN";
            String instanceTable = DOMAIN_PREFIX + "_INSTANCE_DOMAIN";
            String serverImageTable = DOMAIN_PREFIX + "_SERVER_IMAGE_DOMAIN";
            String providerAccountTable =
                DOMAIN_PREFIX + "_PROVIDER_ACCOUNT_DOMAIN";
            String serviceRepositoryTable =
                DOMAIN_PREFIX + "_SERVICE_REPOSITORY_DOMAIN";

            userRepo = new DuracloudUserRepoImpl(dbClientMgr, userTable);
            accountRepo = new DuracloudAccountRepoImpl(dbClientMgr, acctTable);
            rightsRepo = new DuracloudRightsRepoImpl(dbClientMgr, rightsTable);
            userInvitationRepo =
                new DuracloudUserInvitationRepoImpl(dbClientMgr,
                                                    userInvitationTable);
            instanceRepo = new DuracloudInstanceRepoImpl(dbClientMgr, 
                                                         instanceTable);
            serverImageRepo =
                new DuracloudServerImageRepoImpl(dbClientMgr,
                                                 serverImageTable);
            providerAccountRepo =
                new DuracloudProviderAccountRepoImpl(dbClientMgr,
                                                     providerAccountTable);
            serviceRepositoryRepo =
                new DuracloudServiceRepositoryRepoImpl(dbClientMgr,
                                                       serviceRepositoryTable);

        } else {
            userRepo = new DuracloudUserRepoImpl(dbClientMgr);
            accountRepo = new DuracloudAccountRepoImpl(dbClientMgr);
            rightsRepo = new DuracloudRightsRepoImpl(dbClientMgr);
            userInvitationRepo =
                new DuracloudUserInvitationRepoImpl(dbClientMgr);
            instanceRepo = new DuracloudInstanceRepoImpl(dbClientMgr);
            serverImageRepo = new DuracloudServerImageRepoImpl(dbClientMgr);
            providerAccountRepo =
                new DuracloudProviderAccountRepoImpl(dbClientMgr);
            serviceRepositoryRepo =
                new DuracloudServiceRepositoryRepoImpl(dbClientMgr);
        }

        idUtil.initialize(userRepo,
                          accountRepo,
                          rightsRepo,
                          userInvitationRepo,
                          instanceRepo,
                          serverImageRepo,
                          providerAccountRepo,
                          serviceRepositoryRepo);
    }

    @Override
    public DuracloudUserRepo getUserRepo() {
        checkInitialized(this.userRepo, "DuracloudUserRepo");
        return this.userRepo;
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
    public DuracloudProviderAccountRepo getProviderAccountRepo() {
        checkInitialized(this.providerAccountRepo,
                         "DuracloudProviderAccountRepo");
        return this.providerAccountRepo;
    }

    @Override
    public DuracloudServiceRepositoryRepo getServiceRepositoryRepo() {
        checkInitialized(this.serviceRepositoryRepo,
                         "DuracloudServiceRepositoryRepo");
        return this.serviceRepositoryRepo;
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
        repos.add(getAccountRepo());
        repos.add(getRightsRepo());
        repos.add(getUserInvitationRepo());
        repos.add(getInstanceRepo());
        repos.add(getServerImageRepo());
        repos.add(getProviderAccountRepo());
        repos.add(getServiceRepositoryRepo());
        return repos;
    }

    private void checkInitialized(Object member, String name) {
        if (null == member) {
            String msg = name + " is not initialized";
            log.error(msg);
            throw new DBUninitializedException(msg);
        }
    }

}
