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

/**
 * @author Daniel Bernstein
 */
public class DuracloudMillForm {
    @NotNull
    private String dbHost;
    @NotNull
    private Integer dbPort = 3306;
    @NotNull
    private String dbName;
    @NotNull
    private String dbUsername;
    @NotNull
    private String dbPassword;
    @NotNull
    private String auditQueue;
    @NotNull
    private String auditLogSpaceId;
    @NotNull
    private String auditQueueType;
    private String rabbitmqHost;
    private Integer rabbitmqPort = 5672;
    private String rabbitmqVhost;
    private String rabbitmqExchange;
    private String rabbitmqUsername;
    private String rabbitmqPassword;

    private ConstraintViolationException nullConstraintViolationException() {
        return new ConstraintViolationException("may not be null", null);
    }

    private Boolean settingRabbitMQ() {
        return this.auditQueueType.equalsIgnoreCase("RabbitMQ");
    }

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public Integer getDbPort() {
        return dbPort;
    }

    public void setDbPort(Integer dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getAuditQueue() {
        return auditQueue;
    }

    public void setAuditQueue(String auditQueue) {
        this.auditQueue = auditQueue;
    }

    public String getAuditLogSpaceId() {
        return auditLogSpaceId;
    }

    public void setAuditLogSpaceId(String auditLogSpaceId) {
        this.auditLogSpaceId = auditLogSpaceId;
    }

    public String getAuditQueueType() {
        return auditQueueType;
    }

    public void setAuditQueueType(String auditQueueType) {
        this.auditQueueType = auditQueueType;
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
}
