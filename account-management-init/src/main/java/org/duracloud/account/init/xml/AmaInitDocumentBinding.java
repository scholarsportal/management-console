/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.init.xml;

import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.util.EncryptionUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Andrew Woods
 *         Date: Jan 28, 2011
 */
public class AmaInitDocumentBinding {

    private static final Logger log = LoggerFactory.getLogger(
        AmaInitDocumentBinding.class);

    private static EncryptionUtil encryptionUtil;

    /**
     * This method deserializes the provided xml into an AMA config object.
     *
     * @param xml
     * @return
     */
    public static AmaConfig createAmaConfigFrom(InputStream xml) {
        AmaConfig config = new AmaConfig();
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(xml);
            Element root = doc.getRootElement();

            Element credential = root.getChild("credential");
            String encUsername = credential.getChildText("username");
            String encPassword = credential.getChildText("password");
            config.setUsername(decrypt(encUsername));
            config.setPassword(decrypt(encPassword));

            Element admin = root.getChild("admin");
            if (null != admin) {
                Iterator<Element> emails = admin.getChildren().iterator();
                while (emails.hasNext()) {
                    Element email = emails.next();
                    config.addAdminAddress(email.getAttributeValue("id"),
                                           email.getText());
                }
            }

            Element ldap = root.getChild("ldap");
            if (null != ldap) {
                String encLdapUserDn =ldap.getChildText("userdn");
                String encLdapPassword =ldap.getChildText("password");

                config.setLdapBaseDn(ldap.getChildText("basedn"));
                config.setLdapUserDn(decrypt(encLdapUserDn));
                config.setLdapPassword(decrypt(encLdapPassword));
                config.setLdapUrl(ldap.getChildText("url"));
            }

            Element idUtil = root.getChild("idutil");
            if (null != idUtil) {
                config.setIdUtilHost(idUtil.getChildText("host"));
                config.setIdUtilPort(idUtil.getChildText("port"));
                config.setIdUtilCtxt(idUtil.getChildText("ctxt"));

                String encIdUsername = idUtil.getChildText("username");
                String encIdPassword = idUtil.getChildText("password");
                config.setIdUtilUsername(decrypt(encIdUsername));
                config.setIdUtilPassword(decrypt(encIdPassword));
            }

            config.setHost(root.getChildText("host"));
            config.setPort(root.getChildText("port"));
            config.setCtxt(root.getChildText("ctxt"));

        } catch (Exception e) {
            String error = "Error encountered attempting to parse " +
                "AMA configuration xml: " + e.getMessage();
            log.error(error);
            throw new DuraCloudRuntimeException(error, e);
        }

        return config;
    }

    /**
     * This method serializes the provide AMA configuration into xml.
     *
     * @param amaConfig
     * @return
     */
    public static String createDocumentFrom(AmaConfig amaConfig) {
        StringBuilder xml = new StringBuilder();

        if (null != amaConfig) {
            String username = encrypt(amaConfig.getUsername());
            String password = encrypt(amaConfig.getPassword());
            String host = amaConfig.getHost();
            String port = amaConfig.getPort();
            String ctxt = amaConfig.getCtxt();
            String ldapBaseDn = amaConfig.getLdapBaseDn();
            String ldapUserDn = encrypt(amaConfig.getLdapUserDn());
            String ldapPassword = encrypt(amaConfig.getLdapPassword());
            String ldapUrl = amaConfig.getLdapUrl();
            String idUtilHost = amaConfig.getIdUtilHost();
            String idUtilPort = amaConfig.getIdUtilPort();
            String idUtilCtxt = amaConfig.getIdUtilCtxt();
            String idUtilUsername = encrypt(amaConfig.getIdUtilUsername());
            String idUtilPassword = encrypt(amaConfig.getIdUtilPassword());
            Collection emails = amaConfig.getAdminAddresses();

            xml.append("<ama>");
            xml.append("  <credential>");
            xml.append("    <username>" + username + "</username>");
            xml.append("    <password>" + password + "</password>");
            xml.append("  </credential>");


            if (null != emails && emails.size() > 0) {
                int i = 0;
                xml.append("  <admin>");
                Iterator<String> itr = emails.iterator();
                while (itr.hasNext()) {
                    xml.append("    <email id='" + i++ + "'>" + itr.next() +
                                   "</email>");
                }
                xml.append("  </admin>");
            }

            xml.append("  <ldap>");
            xml.append("    <basedn>" + ldapBaseDn + "</basedn>");
            xml.append("    <userdn>" + ldapUserDn + "</userdn>");
            xml.append("    <password>" + ldapPassword + "</password>");
            xml.append("    <url>" + ldapUrl + "</url>");
            xml.append("  </ldap>");

            xml.append("  <idutil>");
            xml.append("    <host>" + idUtilHost + "</host>");
            xml.append("    <port>" + idUtilPort + "</port>");
            xml.append("    <ctxt>" + idUtilCtxt + "</ctxt>");
            xml.append("    <username>" + idUtilUsername + "</username>");
            xml.append("    <password>" + idUtilPassword + "</password>");
            xml.append("  </idutil>");

            xml.append("  <host>" + host + "</host>");
            xml.append("  <port>" + port + "</port>");
            xml.append("  <ctxt>" + ctxt + "</ctxt>");
            xml.append("</ama>");
        }
        return xml.toString();
    }

    private static String encrypt(String text) {
        try {
            return getEncryptionUtil().encrypt(text);
        } catch (Exception e) {
            throw new DuraCloudRuntimeException(e);
        }
    }

    private static String decrypt(String text) {
        try {
            return getEncryptionUtil().decrypt(text);
        } catch (Exception e) {
            throw new DuraCloudRuntimeException(e);
        }
    }

    private static EncryptionUtil getEncryptionUtil() {
        if (null == encryptionUtil) {
            try {
                encryptionUtil = new EncryptionUtil();
            } catch (Exception e) {
                throw new DuraCloudRuntimeException(e);
            }
        }
        return encryptionUtil;
    }
}
