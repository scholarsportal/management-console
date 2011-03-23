/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.sys.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 3/22/11
 */
public class CustomerMonitorImplTest extends EventMonitorTestBase {

    private CustomerMonitorImpl customerMonitor;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        customerMonitor = new CustomerMonitorImpl(notificationMgr);
    }

    @Test
    public void testAccountCreated() throws Exception {
        Capture<String> capturedSubj = new Capture<String>();
        Capture<String> capturedBody = new Capture<String>();
        Capture<String> capturedRecipient0 = new Capture<String>();
        emailer.send(EasyMock.capture(capturedSubj),
                     EasyMock.capture(capturedBody),
                     EasyMock.capture(capturedRecipient0));
        EasyMock.expectLastCall();

        EasyMock.expect(notificationMgr.getEmailer()).andReturn(emailer);
        EasyMock.replay(notificationMgr, emailer);

        String subdomain = "sub-domain";
        String username = "user-name";
        AccountInfo acctInfo = newAccountInfo(subdomain);
        DuracloudUser user = newDuracloudUser(username);

        // call under test
        customerMonitor.accountCreated(acctInfo, user);

        String subj = capturedSubj.getValue();
        Assert.assertNotNull(subj);
        Assert.assertTrue(subj, subj.contains(subdomain));

        String body = capturedBody.getValue();
        Assert.assertNotNull(body);
        Assert.assertTrue(body, body.contains(subdomain));
        Assert.assertTrue(body, body.contains(user.getFirstName()));

        String email0 = capturedRecipient0.getValue();
        Assert.assertNotNull(email0);
        Assert.assertEquals(user.getEmail(), email0);
    }

}
