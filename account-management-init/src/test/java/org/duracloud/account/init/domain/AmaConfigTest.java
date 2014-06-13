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
    private String auditQueue = "audit-queue";
    private String host = "host";
    private String port = "8008";
    private String ctxt = "context";
    private String admin0 = "a@g.com";
    private String admin1 = "x@y.org";
    private String ldapBaseDn = "ldap-base-dn";
    private String ldapUserDn = "ldap-user-dn";
    private String ldapPassword = "ldap-password";
    private String ldapUrl = "ldap-url";
    private String idUtilHost = "id-util-host";
    private String idUtilPort = "id-util-port";
    private String idUtilCtxt = "id-util-context";
    private String idUtilUsername = "id-util-username";
    private String idUtilPassword = "id-util-password";

    private EncryptionUtil encryptionUtil;

    @Before
    public void setUp() throws Exception {
        amaConfig = new AmaConfig();
        amaConfig.setUsername(username);
        amaConfig.setPassword(password);
        amaConfig.setAuditQueue(auditQueue);

        amaConfig.setHost(host);
        amaConfig.setPort(port);
        amaConfig.setCtxt(ctxt);
        amaConfig.addAdminAddress("0", admin0);
        amaConfig.addAdminAddress("1", admin1);
        amaConfig.setLdapBaseDn(ldapBaseDn);
        amaConfig.setLdapUserDn(ldapUserDn);
        amaConfig.setLdapPassword(ldapPassword);
        amaConfig.setLdapUrl(ldapUrl);
        amaConfig.setIdUtilHost(idUtilHost);
        amaConfig.setIdUtilPort(idUtilPort);
        amaConfig.setIdUtilCtxt(idUtilCtxt);
        amaConfig.setIdUtilUsername(idUtilUsername);
        amaConfig.setIdUtilPassword(idUtilPassword);

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

        String encLdapUserDn = encryptionUtil.encrypt(ldapUserDn);
        String encLdapPassword = encryptionUtil.encrypt(ldapPassword);

        Assert.assertTrue(xml.contains(ldapBaseDn));
        Assert.assertTrue(xml.contains(encLdapUserDn));
        Assert.assertTrue(xml.contains(encLdapPassword));
        Assert.assertTrue(xml.contains(ldapUrl));

        String encIdUsername = encryptionUtil.encrypt(idUtilUsername);
        String encIdPassword = encryptionUtil.encrypt(idUtilPassword);

        Assert.assertTrue(xml.contains(idUtilHost));
        Assert.assertTrue(xml.contains(idUtilPort));
        Assert.assertTrue(xml.contains(idUtilCtxt));
        Assert.assertTrue(xml.contains(encIdUsername));
        Assert.assertTrue(xml.contains(encIdPassword));
    }

    @Test
    public void testGetInitResource() throws Exception {
        Assert.assertEquals("/init", amaConfig.getInitResource());
    }
}
