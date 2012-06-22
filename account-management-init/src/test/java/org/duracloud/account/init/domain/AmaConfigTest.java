/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.init.domain;

import org.duracloud.common.util.EncryptionUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: Feb 1, 2011
 */
public class AmaConfigTest {

    private AmaConfig amaConfig;

    private String username = "user-name";
    private String password = "pass-word";
    private String host = "host";
    private String port = "8008";
    private String ctxt = "context";
    private String admin0 = "a@g.com";
    private String admin1 = "x@y.org";
    private String ldapBaseDn = "ldap-base-dn";
    private String ldapUserDn = "ldap-user-dn";
    private String ldapPassword = "ldap-password";
    private String ldapUrl = "ldap-url";

    private EncryptionUtil encryptionUtil;

    @Before
    public void setUp() throws Exception {
        amaConfig = new AmaConfig();
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

        Assert.assertTrue(xml.contains(host));
        Assert.assertTrue(xml.contains(port));
        Assert.assertTrue(xml.contains(ctxt));
        Assert.assertTrue(xml.contains(admin0));
        Assert.assertTrue(xml.contains(admin1));

        Assert.assertTrue(xml.contains(ldapBaseDn));
        Assert.assertTrue(xml.contains(ldapUserDn));
        Assert.assertTrue(xml.contains(ldapPassword));
        Assert.assertTrue(xml.contains(ldapUrl));
    }

    @Test
    public void testGetInitResource() throws Exception {
        Assert.assertEquals("/init", amaConfig.getInitResource());
    }
}
