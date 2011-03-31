/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.sys.impl;

import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.init.domain.AmaConfig;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 3/21/11
 */
public class SystemMonitorImplTest extends EventMonitorTestBase {

    private SystemMonitorImpl systemMonitor;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        AmaConfig config = new AmaConfig();
        for (int i = 0; i < recipients.size(); ++i) {
            config.addAdminAddress(Integer.toString(i), recipients.get(i));
        }

        systemMonitor = new SystemMonitorImpl(notificationMgr);
        systemMonitor.initialize(config);
    }

    @Test
    public void testAccountCreated() throws Exception {
        Capture<String> capturedSubj = new Capture<String>();
        Capture<String> capturedBody = new Capture<String>();
        Capture<String> capturedRecipient0 = new Capture<String>();
        Capture<String> capturedRecipient1 = new Capture<String>();
        emailer.send(EasyMock.capture(capturedSubj),
                     EasyMock.capture(capturedBody),
                     EasyMock.capture(capturedRecipient0),
                     EasyMock.capture(capturedRecipient1));
        EasyMock.expectLastCall();

        EasyMock.expect(notificationMgr.getEmailer()).andReturn(emailer);
        EasyMock.replay(notificationMgr, emailer);

        String subdomain = "sub-domain";
        String username = "user-name";
        AccountCreationInfo acctInfo = newAccountCreationInfo(subdomain);
        DuracloudUser user = newDuracloudUser(username);

        // call under test
        systemMonitor.accountCreated(acctInfo, user);

        String subj = capturedSubj.getValue();
        Assert.assertNotNull(subj);
        Assert.assertTrue(subj, subj.contains(subdomain));

        String body = capturedBody.getValue();
        Assert.assertNotNull(body);
        Assert.assertTrue(body, body.contains(subdomain));
        Assert.assertTrue(body, body.contains(username));

        String email0 = capturedRecipient0.getValue();
        String email1 = capturedRecipient1.getValue();
        Assert.assertNotNull(email0);
        Assert.assertNotNull(email1);

        Assert.assertTrue(email0, recipients.contains(email0));
        Assert.assertTrue(email1, recipients.contains(email1));
    }

}
