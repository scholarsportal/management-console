/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.init.domain;

import org.duracloud.account.init.xml.AmaInitDocumentBinding;
import org.duracloud.appconfig.domain.AppConfig;
import org.duracloud.appconfig.domain.BaseConfig;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Jan 28, 2011
 */
public class AmaConfig extends BaseConfig implements AppConfig {
    private final Logger log = LoggerFactory.getLogger(AmaConfig.class);

    public static final String QUALIFIER = "ama";

    private static final String INIT_RESOURCE = "/init";

    public static final String awsUsernameKey = "username";
    public static final String awsPasswordKey = "password";
    public static final String adminEmailKey = "admin";

    private String awsUsername;
    private String awsPassword;
    private String host;
    private String port;
    private String ctxt;

    private Map<String, String> adminAddresses = new HashMap<String, String>();

    @Override
    protected String getQualifier() {
        return QUALIFIER;
    }

    @Override
    protected void loadProperty(String key, String value) {
        String prefix = getPrefix(key.toLowerCase());
        if (prefix.equalsIgnoreCase(awsUsernameKey)) {
            this.awsUsername = value;

        } else if (prefix.equalsIgnoreCase(awsPasswordKey)) {
            this.awsPassword = value;

        } else if (prefix.equalsIgnoreCase(adminEmailKey)) {
            String id = getSuffix(key);
            adminAddresses.put(id, value);

        } else {
            String msg = "unknown key: " + key + " (" + value + ")";
            log.error(msg);
            throw new DuraCloudRuntimeException(msg);
        }
    }

    @Override
    public String asXml() {
        return AmaInitDocumentBinding.createDocumentFrom(this);
    }

    @Override
    public String getInitResource() {
        return INIT_RESOURCE;
    }

    public String getUsername() {
        return awsUsername;
    }

    public void setUsername(String awsUsername) {
        this.awsUsername = awsUsername;
    }

    public String getPassword() {
        return awsPassword;
    }

    public void setPassword(String awsPassword) {
        this.awsPassword = awsPassword;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getCtxt() {
        return ctxt;
    }

    public void setCtxt(String ctxt) {
        this.ctxt = ctxt;
    }

    public Collection<String> getAdminAddresses() {
        return adminAddresses.values();
    }

    public void addAdminAddress(String id, String adminAddress) {
        this.adminAddresses.put(id, adminAddress);
    }
}
