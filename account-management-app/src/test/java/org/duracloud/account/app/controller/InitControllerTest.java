/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBUninitializedException;
import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.account.init.domain.Initable;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.common.util.EncryptionUtil;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Andrew Woods
 *         Date: Dec 10, 2010
 */
public class InitControllerTest {

    private InitController controller;
    private DuracloudRepoMgr repoMgr;
    private NotificationMgr notificationMgr;
    private Initable systemMonitor;

    private String host = "a-host";
    private String port = "a-port";
    private String ctxt = "a-ctxt";

    @Before
    public void setUp() throws Exception {
        repoMgr = EasyMock.createMock(DuracloudRepoMgr.class);
        notificationMgr = EasyMock.createMock(NotificationMgr.class);
        systemMonitor = EasyMock.createMock("SystemMonitor", Initable.class);
        controller = new InitController();
        controller.setRepoMgr(repoMgr);
        controller.setNotificationMgr(notificationMgr);
        controller.setSystemMonitor(systemMonitor);
    }

    @After
    public void tearDown() {
        EasyMock.verify(repoMgr, notificationMgr, systemMonitor);
    }

    @Test
    public void testInitialize() throws Exception {
        repoMgr.initialize(EasyMock.isA(AmaConfig.class));
        EasyMock.expectLastCall();
        EasyMock.replay(repoMgr);

        notificationMgr.initialize(EasyMock.isA(AmaConfig.class));
        EasyMock.expectLastCall();
        EasyMock.replay(notificationMgr);

        systemMonitor.initialize(EasyMock.isA(AmaConfig.class));
        EasyMock.expectLastCall();
        EasyMock.replay(systemMonitor);

        UserInvitation invitation = new UserInvitation(1, 2, "t", "x", 3, "y");
        String url = invitation.getRedemptionURL();
        Assert.assertFalse(url.contains(host));
        Assert.assertFalse(url.contains(port));
        Assert.assertFalse(url.contains(ctxt));

        ResponseEntity<String> response = controller.initialize(inputStream());
        Assert.assertNotNull(response);

        HttpStatus statusCode = response.getStatusCode();
        Assert.assertEquals(HttpStatus.OK, statusCode);

        url = invitation.getRedemptionURL();
        Assert.assertTrue(url.contains(host));
        Assert.assertTrue(url.contains(port));
        Assert.assertTrue(url.contains(ctxt));
    }

    @Test
    public void testInitializeBad() throws Exception {
        repoMgr.initialize(EasyMock.isA(AmaConfig.class));
        EasyMock.expectLastCall().andThrow(new DBUninitializedException(
            "canned-exception"));
        EasyMock.replay(repoMgr, notificationMgr, systemMonitor);

        ResponseEntity<String> response = controller.initialize(inputStream());
        Assert.assertNotNull(response);

        HttpStatus statusCode = response.getStatusCode();
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);
    }

    private InputStream inputStream() throws Exception {
        EncryptionUtil encrypter = new EncryptionUtil();
        String username = encrypter.encrypt("username");
        String password = encrypter.encrypt("password");

        StringBuilder sb = new StringBuilder();
        sb.append("<ama>");
        sb.append("  <credential>");
        sb.append("    <username>" + username + "</username>");
        sb.append("    <password>" + password + "</password>");
        sb.append("  </credential>");
        sb.append("  <host>" + host + "</host>");
        sb.append("  <port>" + port + "</port>");
        sb.append("  <ctxt>" + ctxt + "</ctxt>");
        sb.append("</ama>");

        byte[] text = sb.toString().getBytes();
        return new AutoCloseInputStream(new ByteArrayInputStream(text));
    }

}
