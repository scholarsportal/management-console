/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.sys.impl;

import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.notification.Emailer;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Woods
 *         Date: 3/22/11
 */
public class EventMonitorTestBase {

    protected NotificationMgr notificationMgr;
    protected Emailer emailer;

    protected List<String> recipients;
    private int acctId;
    private int userId;

    @Before
    public void setUp() throws Exception {
        notificationMgr = EasyMock.createMock("NotificationMgr",
                                              NotificationMgr.class);
        emailer = EasyMock.createMock("Emailer", Emailer.class);

        recipients = new ArrayList<String>();
        recipients.add("a@g.com");
        recipients.add("x@y.org");
        acctId = 0;
        userId = 0;
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(notificationMgr, emailer);
    }

    protected DuracloudUser newDuracloudUser(String username) {
        String fname = username.toUpperCase();
        String email = username + "@g.com";
        return new DuracloudUser(userId++, username, null, fname, null, email,
                                 null, null);
    }

    protected AccountCreationInfo newAccountCreationInfo(String subdomain) {
        return new AccountCreationInfo(subdomain, null, null, null, null, null);
    }
}
