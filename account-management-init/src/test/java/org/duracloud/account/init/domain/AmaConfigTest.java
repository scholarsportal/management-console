/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.init.domain;

import org.duracloud.common.util.EncryptionUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: Feb 1, 2011
 */
public class AmaConfigTest {

    private AmaConfig amaConfig;

    private String username = "user-name";
    private String password = "pass-word";

    private EncryptionUtil encryptionUtil;

    @Before
    public void setUp() throws Exception {
        amaConfig = new AmaConfig();
        amaConfig.setUsername(username);
        amaConfig.setPassword(password);

        encryptionUtil = new EncryptionUtil();
    }

    @Test
    public void testAsXml() throws Exception {
        String xml = amaConfig.asXml();
        Assert.assertNotNull(xml);

        String encUsername = encryptionUtil.encrypt(username);
        String encPassword = encryptionUtil.encrypt(password);
        Assert.assertTrue(xml.contains(encUsername));
        Assert.assertTrue(xml.contains(encPassword));
    }

    @Test
    public void testGetInitResource() throws Exception {
        Assert.assertEquals("/init", amaConfig.getInitResource());
    }
}
