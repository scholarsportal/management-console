/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util.notification;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.notification.Emailer;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    private Capture<String> message;

    @Before
    public void setup() {
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
    }

    @Test
    public void testSendNotificationPasswordReset() {
        mockEmailer.send(EasyMock.isA(String.class),
                         EasyMock.capture(message),
                         EasyMock.eq(email));
        EasyMock.expectLastCall()
            .times(1);
        replayMocks();

        String newPassword = "newpass";
        notifier.sendNotificationPasswordReset(user, newPassword);
        String msg = message.getValue();
        assertNotNull(msg);
        assertTrue("Message should contain new password",
                   msg.contains(newPassword));
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
    }

}
