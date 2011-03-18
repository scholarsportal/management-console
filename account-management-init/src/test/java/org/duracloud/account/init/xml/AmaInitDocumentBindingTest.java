/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.init.xml;

import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.common.util.EncryptionUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Andrew Woods
 *         Date: Feb 1, 2011
 */
public class AmaInitDocumentBindingTest {

    private InputStream xml;
    private String username = "user-name";
    private String password = "pass-word";

    private EncryptionUtil encryptionUtil;

    @Before
    public void setUp() throws Exception {
        encryptionUtil = new EncryptionUtil();
    }

    @After
    public void tearDown() throws IOException {
        if (null != xml) {
            xml.close();
        }
    }

    @Test
    public void testCreateAmaConfigFrom() throws Exception {
        xml = createInputStream();
        AmaConfig amaConfig = AmaInitDocumentBinding.createAmaConfigFrom(xml);
        Assert.assertNotNull(amaConfig);

        Assert.assertEquals(username, amaConfig.getUsername());
        Assert.assertEquals(password, amaConfig.getPassword());

        Assert.assertEquals("/init", amaConfig.getInitResource());
    }

    private InputStream createInputStream() throws Exception {
        String encUsername = encryptionUtil.encrypt(username);
        String encPassword = encryptionUtil.encrypt(password);

        StringBuilder sb = new StringBuilder();
        sb.append("<credential>");
        sb.append("  <username>" + encUsername + "</username>");
        sb.append("  <password>" + encPassword + "</password>");
        sb.append("</credential>");

        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    @Test
    public void testCreateDocumentFrom() throws Exception {
        AmaConfig amaConfig = new AmaConfig();
        amaConfig.setUsername(username);
        amaConfig.setPassword(password);

        String doc = AmaInitDocumentBinding.createDocumentFrom(amaConfig);
        Assert.assertNotNull(doc);

        String encUsername = encryptionUtil.encrypt(username);
        String encPassword = encryptionUtil.encrypt(password);
        Assert.assertTrue(doc.contains(encUsername));
        Assert.assertTrue(doc.contains(encPassword));
    }
}
