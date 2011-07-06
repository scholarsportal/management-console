/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup.util.impl;

import org.duracloud.notification.Emailer;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Woods
 *         Date: 7/6/11
 */
public class EmailUtilImplTest {

    private EmailUtilImpl emailUtil;

    private Emailer emailer;
    private List<String> recipients;

    @Before
    public void setUp() throws Exception {
        recipients = new ArrayList<String>();
        recipients.add("a@g.com");
        recipients.add("x@y.org");

        emailer = EasyMock.createMock("Emailer", Emailer.class);

        emailUtil = new EmailUtilImpl(emailer, recipients);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(emailer);
    }

    private void replayMocks() {
        EasyMock.replay(emailer);
    }

    @Test
    public void testSendEmail() throws Exception {
        String subject = "subject";
        String body = "body";
        String[] toAddresses = recipients.toArray(new String[]{});
        emailer.send(subject, body, toAddresses);
        EasyMock.expectLastCall();

        replayMocks();

        emailUtil.sendEmail(subject, body);
    }
}
