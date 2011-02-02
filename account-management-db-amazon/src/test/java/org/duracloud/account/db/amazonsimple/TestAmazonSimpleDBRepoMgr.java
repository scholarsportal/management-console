/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.impl.IdUtilImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.common.util.EncryptionUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Andrew Woods
 *         Date: Dec 9, 2010
 */
public class TestAmazonSimpleDBRepoMgr extends BaseTestDuracloudRepoImpl {

    private AmazonSimpleDBRepoMgr repoMgr;
    private IdUtil idUtil;
    private InputStream xml;

    @Before
    public void setUp() throws Exception {
        idUtil = new IdUtilImpl();
        repoMgr = new AmazonSimpleDBRepoMgr(idUtil);
    }

    @After
    public void tearDown() throws IOException {
        if (null != xml) {
            xml.close();
        }
    }

    @Test
    public void testInitialize() throws Exception {
        String text = initializationXml();
        xml = new ByteArrayInputStream(text.getBytes());
        repoMgr.initialize(xml);

        // No exceptions indicates success.
        repoMgr.getUserRepo();
        repoMgr.getAccountRepo();
        repoMgr.getRightsRepo();
        repoMgr.getUserInvitationRepo();
        repoMgr.getInstanceRepo();
        repoMgr.getServerImageRepo();
        repoMgr.getProviderAccountRepo();

        // IdUtil only throws when a direct call is made.
        repoMgr.getIdUtil().newAccountId();
    }

    private String initializationXml() throws Exception {
        EncryptionUtil encrypter = new EncryptionUtil();
        Credential cred = getCredential();
        String username = encrypter.encrypt(cred.getUsername());
        String password = encrypter.encrypt(cred.getPassword());

        StringBuilder sb = new StringBuilder();
        sb.append("<credential>");
        sb.append("  <username>" + username + "</username>");
        sb.append("  <password>" + password + "</password>");
        sb.append("</credential>");

        return sb.toString();
    }
}
