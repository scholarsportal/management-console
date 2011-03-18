/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.impl.IdUtilImpl;
import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.common.model.Credential;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Andrew Woods
 *         Date: Dec 9, 2010
 */
public class TestAmazonSimpleDBRepoMgr extends BaseTestDuracloudRepoImpl {

    private AmazonSimpleDBRepoMgr repoMgr;
    private IdUtil idUtil;

    private String TEST_PREFIX = "TEST_DOMAIN";

    @Before
    public void setUp() throws Exception {
        idUtil = new IdUtilImpl();
        repoMgr = new AmazonSimpleDBRepoMgr(idUtil, TEST_PREFIX);
    }

    @Test
    public void testInitialize() throws Exception {
        repoMgr.initialize(amaConfig());

        // No exceptions indicates success.
        repoMgr.getUserRepo();
        repoMgr.getAccountRepo();
        repoMgr.getRightsRepo();
        repoMgr.getUserInvitationRepo();
        repoMgr.getInstanceRepo();
        repoMgr.getServerImageRepo();
        repoMgr.getProviderAccountRepo();
        repoMgr.getServiceRepositoryRepo();

        // IdUtil only throws when a direct call is made.
        repoMgr.getIdUtil().newAccountId();
    }

    private AmaConfig amaConfig() throws Exception {
        Credential cred = getCredential();
        String username = cred.getUsername();
        String password = cred.getPassword();

        AmaConfig config = new AmaConfig();
        config.setUsername(username);
        config.setPassword(password);

        return config;
    }
}
