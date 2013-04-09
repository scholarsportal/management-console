/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.notification;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.duracloud.account.common.domain.AmaEndpoint;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.notification.Emailer;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: Bill Branan
 * Date: 8/1/11
 */
public class NotifierTest {

    private Notifier notifier;
    private Emailer mockEmailer;
    private DuracloudUser user;
    private static final String username = "username";
    private static final String email = "email@address.comn";
    private static final String host = "junkhost";
    private Capture<String> message;

    @Before
    public void setup() {
        AmaEndpoint.initialize(host, "443", "abc");
        mockEmailer = EasyMock.createMock(Emailer.class);
        notifier = new Notifier(mockEmailer);
        message = new Capture<String>();
        user = new DuracloudUser(0,
                                 username,
                                 "password",
                                 "firstName",
                                 "lastName",
                                 email,
                                 "securityQuestion",
                                 "securityAnswer");
    }

    private void replayMocks() {
        EasyMock.replay(mockEmailer);
    }

    @After
    public void teardown() {
        EasyMock.verify(mockEmailer);
    }

    @Test
    public void testSendNotificationCreateNewUser() {
        mockEmailer.send(EasyMock.isA(String.class),
                         EasyMock.capture(message),
                         EasyMock.eq(email));
        EasyMock.expectLastCall()
            .times(1);
        replayMocks();

        notifier.sendNotificationCreateNewUser(user);
        String msg = message.getValue();
        assertNotNull(msg);
        assertTrue("Message should contain username", msg.contains(username));
        assertTrue(host + " :: " + msg, msg.contains(host));
        assertTrue(msg, !msg.contains("manage.duracloud.org"));
    }

    @Test
    public void testSendNotificationPasswordReset() {
        mockEmailer.send(EasyMock.isA(String.class),
                         EasyMock.capture(message),
                         EasyMock.eq(email));
        EasyMock.expectLastCall()
            .times(1);
        replayMocks();

        String redemptionCode = "redemptionCode";
        notifier.sendNotificationPasswordReset(user, redemptionCode, new Date());
        String msg = message.getValue();
        assertNotNull(msg);
        assertTrue("Message should contain redemptionCode",
                   msg.contains(redemptionCode));
        assertTrue(host + " :: " + msg, msg.contains(host));
        assertTrue(msg, !msg.contains("manage.duracloud.org"));
    }

    @Test
    public void testSendNotificationRedeemedInvitation() {
        String adminEmail = "admin@email.com";
        mockEmailer.send(EasyMock.isA(String.class),
                         EasyMock.capture(message),
                         EasyMock.eq(adminEmail));
        EasyMock.expectLastCall()
            .times(1);
        replayMocks();

        notifier.sendNotificationRedeemedInvitation(user, adminEmail);
        String msg = message.getValue();
        assertNotNull(msg);
        assertTrue("Message should contain username", msg.contains(username));
        assertTrue(host + " :: " + msg, msg.contains(host));
        assertTrue(msg, !msg.contains("manage.duracloud.org"));
    }

}
