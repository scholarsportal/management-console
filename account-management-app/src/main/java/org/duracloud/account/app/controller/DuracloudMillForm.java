/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author Daniel Bernstein
 *
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
    
    
}
