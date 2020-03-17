/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import org.duracloud.common.changenotifier.NotifierType;

/**
 * @author Daniel Bernstein
 */
public class GlobalPropertiesForm {
    @NotNull
    private String notifierType = NotifierType.SNS.toString();
    private String rabbitmqHost;
    private Integer rabbitmqPort = 5672;
    private String rabbitmqVhost = "/";
    private String rabbitmqExchange;
    private String rabbitmqUsername;
    private String rabbitmqPassword;
    private String instanceNotificationTopicArn;
    @NotNull(message = "You must specify a CloudFront Account Id")
    private String cloudFrontAccountId;
    @NotNull(message = "You must specify a Cloud Key Id")
    private String cloudFrontKeyId;
    @NotNull(message = "You must specify a CloudFront Key Path")
    private String cloudFrontKeyPath;

    private ConstraintViolationException nullConstraintViolationException() {
        return new ConstraintViolationException("may not be null", null);
    }

    private Boolean settingRabbitMQ() {
        return this.notifierType.equalsIgnoreCase(NotifierType.RABBITMQ.toString());
    }

    public void setNotifierType(String notifierType) {
        this.notifierType = notifierType;
        if (settingRabbitMQ()) {
            setInstanceNotificationTopicArn(null);
        }
    }

    public String getNotifierType() {
        return notifierType;
    }

    public String getRabbitmqHost() {
        return rabbitmqHost;
    }

    public void setRabbitmqHost(String rabbitmqHost) {
        if (settingRabbitMQ()) {
            if (rabbitmqHost == null) {
                throw nullConstraintViolationException();
            } else {
                this.rabbitmqHost = rabbitmqHost;
            }
        } else {
            this.rabbitmqHost = null;
        }
    }

    public Integer getRabbitmqPort() {
        return rabbitmqPort;
    }

    public void setRabbitmqPort(Integer rabbitmqPort) {
        if (settingRabbitMQ()) {
            if (rabbitmqPort == null) {
                throw nullConstraintViolationException();
            } else {
                this.rabbitmqPort = rabbitmqPort;
            }
        } else {
            this.rabbitmqPort = 5672;
        }
    }

    public String getRabbitmqVhost() {
        return rabbitmqVhost;
    }

    public void setRabbitmqVhost(String rabbitmqVhost) {
        if (settingRabbitMQ()) {
            if (rabbitmqVhost == null) {
                throw nullConstraintViolationException();
            } else {
                this.rabbitmqVhost = rabbitmqVhost;
            }
        } else {
            this.rabbitmqVhost = "/";
        }
    }

    public String getRabbitmqExchange() {
        return rabbitmqExchange;
    }

    public void setRabbitmqExchange(String rabbitmqExchange) {
        if (settingRabbitMQ()) {
            if (rabbitmqExchange == null) {
                throw nullConstraintViolationException();
            } else {
                this.rabbitmqExchange = rabbitmqExchange;
            }
        } else {
            this.rabbitmqExchange = null;
        }
    }

    public String getRabbitmqUsername() {
        return rabbitmqUsername;
    }

    public void setRabbitmqUsername(String rabbitmqUsername) {
        if (settingRabbitMQ()) {
            if (rabbitmqUsername == null) {
                throw nullConstraintViolationException();
            } else {
                this.rabbitmqUsername = rabbitmqUsername;
            }
        } else {
            this.rabbitmqUsername = null;
        }
    }

    public String getRabbitmqPassword() {
        return rabbitmqPassword;
    }

    public void setRabbitmqPassword(String rabbitmqPassword) {
        if (settingRabbitMQ()) {
            if (rabbitmqPassword == null) {
                throw nullConstraintViolationException();
            } else {
                this.rabbitmqPassword = rabbitmqPassword;
            }
        } else {
            this.rabbitmqPassword = null;
        }
    }

    public String getInstanceNotificationTopicArn() {
        return instanceNotificationTopicArn;
    }

    public void setInstanceNotificationTopicArn(String instanceNotificationTopicArn) {
        this.instanceNotificationTopicArn = instanceNotificationTopicArn;
    }

    public String getCloudFrontAccountId() {
        return cloudFrontAccountId;
    }

    public void setCloudFrontAccountId(String cloudFrontAccountId) {
        this.cloudFrontAccountId = cloudFrontAccountId;
    }

    public String getCloudFrontKeyId() {
        return cloudFrontKeyId;
    }

    public void setCloudFrontKeyId(String cloudFrontKeyId) {
        this.cloudFrontKeyId = cloudFrontKeyId;
    }

    public String getCloudFrontKeyPath() {
        return cloudFrontKeyPath;
    }

    public void setCloudFrontKeyPath(String cloudFrontKeyPath) {
        this.cloudFrontKeyPath = cloudFrontKeyPath;
    }
}
