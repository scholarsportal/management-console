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
    public static String auditKey = "audit";
    public static String auditQueueKey = "queue";

    public static final String adminEmailKey = "admin";
    public static final String ldapKey = "ldap";

    protected static final String ldapBaseDnKey = "basedn";
    protected static final String ldapUserDnKey = "userdn";
    protected static final String ldapPasswordKey = "password";
    protected static final String ldapUrlKey = "url";

    protected final static String idUtilKey = "idutil";
    protected final static String idUtilHostKey = "host";
    protected final static String idUtilPortKey = "port";
    protected final static String idUtilCtxtKey = "ctxt";
    protected final static String idUtilUsernameKey = "username";
    protected final static String idUtilPasswordKey = "password";

    private String awsUsername;
    private String awsPassword;
    private String auditQueue;
    private String host;
    private String port;
    private String ctxt;
    private String ldapBaseDn;
    private String ldapUserDn;
    private String ldapPassword;
    private String ldapUrl;
    private String idUtilHost;
    private String idUtilPort;
    private String idUtilCtxt;
    private String idUtilUsername;
    private String idUtilPassword;

    private Map<String, String> adminAddresses = new HashMap<String, String>();

    @Override
    protected String getQualifier() {
        return QUALIFIER;
    }

    @Override
    protected void loadProperty(String key, String value) {
        String prefix = getPrefix(key.toLowerCase());
        String suffix = getSuffix(key.toLowerCase());

        if (prefix.equalsIgnoreCase(awsUsernameKey)) {
            this.awsUsername = value;

        } else if (prefix.equalsIgnoreCase(awsPasswordKey)) {
            this.awsPassword = value;

        } else if (prefix.equalsIgnoreCase(auditKey)) {
            if(suffix.equalsIgnoreCase(auditQueueKey)){
                this.auditQueue = value;
            }
        	
        } else if (prefix.equalsIgnoreCase(adminEmailKey)) {
            String id = suffix;
            adminAddresses.put(id, value);

        } else if (prefix.equalsIgnoreCase(ldapKey)) {
            loadLdap(suffix, value);

        } else if (prefix.equalsIgnoreCase(idUtilKey)) {
            loadIdUtil(suffix, value);

        } else {
            String msg = "unknown key: " + key + " (" + value + ")";
            log.error(msg);
            throw new DuraCloudRuntimeException(msg);
        }
    }

    private void loadLdap(String key, String value) {
        if (key.equalsIgnoreCase(ldapBaseDnKey)) {
            this.ldapBaseDn = value;

        } else if (key.equalsIgnoreCase(ldapUserDnKey)) {
            this.ldapUserDn = value;

        } else if (key.equalsIgnoreCase(ldapPasswordKey)) {
            this.ldapPassword = value;

        } else if (key.equalsIgnoreCase(ldapUrlKey)) {
            this.ldapUrl = value;

        } else {
            String msg = "unknown ldap key: " + key + " (" + value + ")";
            log.error(msg);
            throw new DuraCloudRuntimeException(msg);
        }
    }

    private void loadIdUtil(String key, String value) {
        if (key.equalsIgnoreCase(idUtilHostKey)) {
            idUtilHost = value;

        } else if (key.equalsIgnoreCase(idUtilPortKey)) {
            idUtilPort = value;

        } else if (key.equalsIgnoreCase(idUtilCtxtKey)) {
            idUtilCtxt = value;

        } else if (key.equalsIgnoreCase(idUtilUsernameKey)) {
            idUtilUsername = value;

        } else if (key.equalsIgnoreCase(idUtilPasswordKey)) {
            idUtilPassword = value;

        } else {
            String msg = "unknown idUtil key: " + key + " (" + value + ")";
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

    public String getAuditQueue(){
    	return this.auditQueue;
    }
    
	public void setAuditQueue(String auditQueue) {
		this.auditQueue = auditQueue;
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

    public String getLdapBaseDn() {
        return ldapBaseDn;
    }

    public void setLdapBaseDn(String ldapBaseDn) {
        this.ldapBaseDn = ldapBaseDn;
    }

    public String getLdapUserDn() {
        return ldapUserDn;
    }

    public void setLdapUserDn(String ldapUserDn) {
        this.ldapUserDn = ldapUserDn;
    }

    public String getLdapPassword() {
        return ldapPassword;
    }

    public void setLdapPassword(String ldapPassword) {
        this.ldapPassword = ldapPassword;
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public String getIdUtilHost() {
        return idUtilHost;
    }

    public void setIdUtilHost(String idUtilHost) {
        this.idUtilHost = idUtilHost;
    }

    public String getIdUtilPort() {
        return idUtilPort;
    }

    public void setIdUtilPort(String idUtilPort) {
        this.idUtilPort = idUtilPort;
    }

    public String getIdUtilCtxt() {
        return idUtilCtxt;
    }

    public void setIdUtilCtxt(String idUtilCtxt) {
        this.idUtilCtxt = idUtilCtxt;
    }

    public String getIdUtilUsername() {
        return idUtilUsername;
    }

    public void setIdUtilUsername(String idUtilUsername) {
        this.idUtilUsername = idUtilUsername;
    }

    public String getIdUtilPassword() {
        return idUtilPassword;
    }

    public void setIdUtilPassword(String idUtilPassword) {
        this.idUtilPassword = idUtilPassword;
    }

}
