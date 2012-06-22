/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.hybrid;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;
import org.duracloud.account.db.DuracloudAccountClusterRepo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudGroupRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudServerDetailsRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudServiceRepositoryRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.amazonsimple.AmazonSimpleDBClientMgr;
import org.duracloud.account.init.domain.AmaConfig;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.directory.DirContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.duracloud.account.db.ldap.domain.LdapRdn.GROUP_OU;
import static org.duracloud.account.db.ldap.domain.LdapRdn.PEOPLE_OU;
import static org.duracloud.account.db.ldap.domain.LdapRdn.RIGHTS_OU;

/**
 * @author Andrew Woods
 *         Date: 6/21/12
 */
public class HybridDBRepoMgrTest {

    private HybridDBRepoMgr repoMgr;
    private IdUtil idUtil;

    private LdapTemplate ldapTemplate;
    private AmazonSimpleDBClientMgr dbClientMgr;

    private static final String awsUsername = "";
    private static final String awsPassword = "";
    private static final String host = "";
    private static final String port = "";
    private static final String ctxt = "";
    private static final String ldapBaseDn = "";
    private static final String ldapUserDn = "";
    private static final String ldapPassword = "";
    private static final String ldapUrl = "ldap://test.org:389";

    final String DOMAIN_PREFIX = "p-";

    @Before
    public void setUp() throws Exception {
        idUtil = EasyMock.createMock("IdUtil", IdUtil.class);
        dbClientMgr = EasyMock.createMock("AmazonSimpleDBClientMgr",
                                          AmazonSimpleDBClientMgr.class);
        ldapTemplate = EasyMock.createMock("LdapTemplate", LdapTemplate.class);

        repoMgr = new HybridDBRepoMgr(idUtil, DOMAIN_PREFIX);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(idUtil, ldapTemplate, dbClientMgr);
    }

    private void replayMocks() {
        EasyMock.replay(idUtil, ldapTemplate, dbClientMgr);
    }

    @Test
    public void testInitialize() throws Exception {
        createLdapMocks();
        createSimpleDbMocks();
        createIdUtilMocks();

        replayMocks();
        AmaConfig config = new AmaConfig();
        config.setCtxt(ctxt);
        config.setHost(host);
        config.setPort(port);
        config.setUsername(awsUsername);
        config.setPassword(awsPassword);
        config.setLdapBaseDn(ldapBaseDn);
        config.setLdapUserDn(ldapUserDn);
        config.setLdapPassword(ldapPassword);
        config.setLdapUrl(ldapUrl);

        repoMgr.setDbClientMgr(dbClientMgr);
        repoMgr.setLdapTemplate(ldapTemplate);
        repoMgr.initialize(config);
    }

    private void createLdapMocks() {
        EasyMock.expect(ldapTemplate.lookup("")).andReturn(null).times(3);
        EasyMock.expect(ldapTemplate.lookup(PEOPLE_OU.toString())).andReturn(
            null);
        EasyMock.expect(ldapTemplate.lookup(GROUP_OU.toString()))
                .andReturn(null);
        EasyMock.expect(ldapTemplate.lookup(RIGHTS_OU.toString())).andReturn(
            null);

        DirContext dirContext = EasyMock.createNiceMock("DirContext",
                                                        DirContext.class);

        ContextSource contextSource = EasyMock.createMock("ContextSource",
                                                          ContextSource.class);
        EasyMock.expect(contextSource.getReadOnlyContext())
                .andReturn(dirContext)
                .times(3);
        EasyMock.expect(ldapTemplate.getContextSource())
                .andReturn(contextSource)
                .times(2);

        EasyMock.replay(contextSource, dirContext);
    }

    private void createSimpleDbMocks() {
        final int numAmazonTables = 9;

        AmazonSimpleDB simpleDB = EasyMock.createMock("AmazonSimpleDB",
                                                      AmazonSimpleDB.class);
        EasyMock.expect(dbClientMgr.getClient()).andReturn(simpleDB).times(
            numAmazonTables);

        ListDomainsResult domains = new ListDomainsResult();
        domains.setDomainNames(getAllDomainNames());
        EasyMock.expect(simpleDB.listDomains(EasyMock.<ListDomainsRequest>anyObject()))
                .andReturn(domains)
                .times(numAmazonTables);

        EasyMock.replay(simpleDB);
    }

    public Collection<String> getAllDomainNames() {
        String acctTable = DOMAIN_PREFIX + "_ACCT_DOMAIN";
        String userInvitationTable = DOMAIN_PREFIX + "_USER_INVITATION_DOMAIN";
        String instanceTable = DOMAIN_PREFIX + "_INSTANCE_DOMAIN";
        String serverImageTable = DOMAIN_PREFIX + "_SERVER_IMAGE_DOMAIN";
        String computeProviderAccountTable =
            DOMAIN_PREFIX + "_COMPUTE_PROVIDER_ACCOUNT_DOMAIN";
        String storageProviderAccountTable =
            DOMAIN_PREFIX + "_STORAGE_PROVIDER_ACCOUNT_DOMAIN";
        String serviceRepositoryTable =
            DOMAIN_PREFIX + "_SERVICE_REPOSITORY_DOMAIN";
        String serverDetailsTable = DOMAIN_PREFIX + "_SERVER_DETAILS_DOMAIN";
        String accountClusterTable = DOMAIN_PREFIX + "_ACCOUNT_CLUSTER_DOMAIN";

        Set<String> domains = new HashSet<String>();
        domains.add(acctTable);
        domains.add(userInvitationTable);
        domains.add(instanceTable);
        domains.add(serverImageTable);
        domains.add(computeProviderAccountTable);
        domains.add(storageProviderAccountTable);
        domains.add(serviceRepositoryTable);
        domains.add(serverDetailsTable);
        domains.add(accountClusterTable);

        return domains;
    }

    private void createIdUtilMocks() {
        idUtil.initialize(EasyMock.isA(DuracloudUserRepo.class),
                          EasyMock.isA(DuracloudGroupRepo.class),
                          EasyMock.isA(DuracloudAccountRepo.class),
                          EasyMock.isA(DuracloudRightsRepo.class),
                          EasyMock.isA(DuracloudUserInvitationRepo.class),
                          EasyMock.isA(DuracloudInstanceRepo.class),
                          EasyMock.isA(DuracloudServerImageRepo.class),
                          EasyMock.isA(DuracloudComputeProviderAccountRepo.class),
                          EasyMock.isA(DuracloudStorageProviderAccountRepo.class),
                          EasyMock.isA(DuracloudServiceRepositoryRepo.class),
                          EasyMock.isA(DuracloudServerDetailsRepo.class),
                          EasyMock.isA(DuracloudAccountClusterRepo.class));
        EasyMock.expectLastCall();
    }
}
