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
import java.util.Collection;

/**
 * @author Andrew Woods
 *         Date: Feb 1, 2011
 */
public class AmaInitDocumentBindingTest {

    private InputStream xml;
    private String username = "user-name";
    private String password = "pass-word";
    private String host = "host";
    private String port = "8765";
    private String ctxt = "ctxt";
    private String admin0 = "a@g.com";
    private String admin1 = "x@y.org";
    private String ldapBaseDn = "ldap-base-dn";
    private String ldapUserDn = "ldap-user-dn";
    private String ldapPassword = "ldap-password";
    private String ldapUrl = "ldap-url";

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
        Assert.assertEquals(host, amaConfig.getHost());
        Assert.assertEquals(port, amaConfig.getPort());
        Assert.assertEquals(ctxt, amaConfig.getCtxt());

        Collection<String> admins = amaConfig.getAdminAddresses();
        Assert.assertEquals(2, admins.size());
        Assert.assertTrue(admins.contains(admin0));
        Assert.assertTrue(admins.contains(admin1));

        Assert.assertEquals(ldapBaseDn, amaConfig.getLdapBaseDn());
        Assert.assertEquals(ldapUserDn, amaConfig.getLdapUserDn());
        Assert.assertEquals(ldapPassword, amaConfig.getLdapPassword());
        Assert.assertEquals(ldapUrl, amaConfig.getLdapUrl());

        Assert.assertEquals("/init", amaConfig.getInitResource());
    }

    private InputStream createInputStream() throws Exception {
        String encUsername = encryptionUtil.encrypt(username);
        String encPassword = encryptionUtil.encrypt(password);
        String encLdapUserDn = encryptionUtil.encrypt(ldapUserDn);
        String encLdapPassword = encryptionUtil.encrypt(ldapPassword);

        StringBuilder sb = new StringBuilder();
        sb.append("<ama>");
        sb.append("  <credential>");
        sb.append("    <username>" + encUsername + "</username>");
        sb.append("    <password>" + encPassword + "</password>");
        sb.append("  </credential>");
        sb.append("  <admin>");
        sb.append("    <email id='0'>" + admin0 + "</email>");
        sb.append("    <email id='1'>" + admin1 + "</email>");
        sb.append("  </admin>");
        sb.append("  <host>" + host + "</host>");
        sb.append("  <port>" + port + "</port>");
        sb.append("  <ctxt>" + ctxt + "</ctxt>");
        sb.append("  <ldap>");
        sb.append("    <basedn>" + ldapBaseDn + "</basedn>");
        sb.append("    <userdn>" + encLdapUserDn + "</userdn>");
        sb.append("    <password>" + encLdapPassword + "</password>");
        sb.append("    <url>" + ldapUrl + "</url>");
        sb.append("  </ldap>");
        sb.append("</ama>");

        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    @Test
    public void testCreateDocumentFrom() throws Exception {
        AmaConfig amaConfig = new AmaConfig();
        amaConfig.setUsername(username);
        amaConfig.setPassword(password);
        amaConfig.setHost(host);
        amaConfig.setPort(port);
        amaConfig.setCtxt(ctxt);
        amaConfig.addAdminAddress("0", admin0);
        amaConfig.addAdminAddress("1", admin1);
        amaConfig.setLdapBaseDn(ldapBaseDn);
        amaConfig.setLdapUserDn(ldapUserDn);
        amaConfig.setLdapPassword(ldapPassword);
        amaConfig.setLdapUrl(ldapUrl);

        String doc = AmaInitDocumentBinding.createDocumentFrom(amaConfig);
        Assert.assertNotNull(doc);

        String encUsername = encryptionUtil.encrypt(username);
        String encPassword = encryptionUtil.encrypt(password);
        Assert.assertTrue(doc.contains(encUsername));
        Assert.assertTrue(doc.contains(encPassword));

        Assert.assertTrue(doc.contains(host));
        Assert.assertTrue(doc.contains(port));
        Assert.assertTrue(doc.contains(ctxt));
        Assert.assertTrue(doc.contains(admin0));
        Assert.assertTrue(doc.contains(admin1));

        String encLdapUserDn = encryptionUtil.encrypt(ldapUserDn);
        String encLdapPassword = encryptionUtil.encrypt(ldapPassword);
        Assert.assertTrue(doc.contains(ldapBaseDn));
        Assert.assertTrue(doc.contains(encLdapUserDn));
        Assert.assertTrue(doc.contains(encLdapPassword));
        Assert.assertTrue(doc.contains(ldapUrl));
    }
}
