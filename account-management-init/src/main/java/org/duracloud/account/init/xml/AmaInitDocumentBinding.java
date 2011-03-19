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

            xml.append("<ama>");
            xml.append("  <credential>");
            xml.append("    <username>" + username + "</username>");
            xml.append("    <password>" + password + "</password>");
            xml.append("  </credential>");
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
