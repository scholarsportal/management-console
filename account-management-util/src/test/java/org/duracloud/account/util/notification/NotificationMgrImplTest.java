/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.notification;

import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.notification.Emailer;
import org.duracloud.notification.NotificationFactory;
import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 3/17/11
 */
public class NotificationMgrImplTest {

    private NotificationMgrImpl notificationMgr;

    private NotificationFactory factory;
    private String fromAddress = "a@b.com";


    @Before
    public void setUp() throws Exception {
        factory = EasyMock.createMock("NotificationFactory",
                                      NotificationFactory.class);
        notificationMgr = new NotificationMgrImpl(factory, fromAddress);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(factory);
    }

    @Test
    public void testInitialize() throws Exception {
        AmaConfig config = amaConfig();

        factory.initialize(config.getUsername(), config.getPassword());
        EasyMock.expectLastCall();
        EasyMock.replay(factory);

        notificationMgr.initialize(config);
    }

    @Test
    public void testGetEmailerInvalid() throws Exception {
        EasyMock.replay(factory);

        boolean threw = false;
        try {
            notificationMgr.getEmailer();
            Assert.fail("Exception expected");

        } catch (Exception e) {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void testGetEmailer() throws Exception {
        AmaConfig config = amaConfig();

        factory.initialize(config.getUsername(), config.getPassword());
        EasyMock.expectLastCall();

        Emailer mockEmailer = EasyMock.createMock("Emailer", Emailer.class);
        EasyMock.expect(factory.getEmailer(fromAddress)).andReturn(mockEmailer);
        EasyMock.replay(factory);

        notificationMgr.initialize(amaConfig());

        Emailer emailer = notificationMgr.getEmailer();
        Assert.assertNotNull(emailer);
    }

    private AmaConfig amaConfig() {
        String username = "username";
        String password = "password";

        AmaConfig config = new AmaConfig();
        config.setUsername(username);
        config.setPassword(password);

        return config;
    }
}
