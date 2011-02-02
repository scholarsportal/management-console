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

/**
 * @author Andrew Woods
 *         Date: Jan 28, 2011
 */
public class AmaConfig extends BaseConfig implements AppConfig {
    private final Logger log = LoggerFactory.getLogger(AmaConfig.class);

    public static final String QUALIFIER = "ama";

    private static final String INIT_RESOURCE = "/init";

    public static final String awsUsernameKey = "aws-username";
    public static final String awsPasswordKey = "aws-password";

    private String awsUsername;
    private String awsPassword;

    @Override
    protected String getQualifier() {
        return QUALIFIER;
    }

    @Override
    protected void loadProperty(String key, String value) {
        key = key.toLowerCase();
        if (key.equalsIgnoreCase(awsUsernameKey)) {
            this.awsUsername = value;

        } else if (key.equalsIgnoreCase(awsPasswordKey)) {
            this.awsPassword = value;

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

    public String getAwsUsername() {
        return awsUsername;
    }

    public void setAwsUsername(String awsUsername) {
        this.awsUsername = awsUsername;
    }

    public String getAwsPassword() {
        return awsPassword;
    }

    public void setAwsPassword(String awsPassword) {
        this.awsPassword = awsPassword;
    }
}
