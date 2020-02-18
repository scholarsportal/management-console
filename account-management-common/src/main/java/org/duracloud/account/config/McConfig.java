/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.config;

/**
 * Configuration properties for the Management Console
 *
 * @author Bill Branan
 * Date: 6/18/14
 */
public class McConfig {

    private String mcHost;
    private String mcPort;
    private String mcContext;
    private String mcDomain;

    private String notificationType;
    private String notificationUser;
    private String notificationPass;
    private String notificationHost;
    private String notificationPort;
    private String notificationFromAddress;
    private String notificationAdminAddress;

    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbUser;
    private String dbPass;

    private String swiftAccessKey;
    private String swiftSecretKey;
    private String swiftEndpoint;
    private String swiftSignerType;

    public McConfig(String mcHost,
                    String mcPort,
                    String mcContext,
                    String mcDomain,
                    String notificationType,
                    String notificationUser,
                    String notificationPass,
                    String notificationHost,
                    String notificationPort,
                    String notificationFromAddress,
                    String notificationAdminAddress,
                    String dbHost,
                    String dbPort,
                    String dbName,
                    String dbUser,
                    String dbPass,
                    String swiftAccessKey,
                    String swiftSecretKey,
                    String swiftEndpoint,
                    String swiftSignerType) {
        this.mcHost = mcHost;
        this.mcPort = mcPort;
        this.mcContext = mcContext;
        this.mcDomain = mcDomain;
        this.notificationType = notificationType;
        this.notificationUser = notificationUser;
        this.notificationPass = notificationPass;
        this.notificationHost = notificationHost;
        this.notificationPort = notificationPort;
        this.notificationFromAddress = notificationFromAddress;
        this.notificationAdminAddress = notificationAdminAddress;
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.swiftAccessKey = swiftAccessKey;
        this.swiftSecretKey = swiftSecretKey;
        this.swiftEndpoint = swiftEndpoint;
        this.swiftSignerType = swiftSignerType;
    }

    public String getMcHost() {
        return mcHost;
    }

    public String getMcPort() {
        return mcPort;
    }

    public String getMcContext() {
        return mcContext;
    }

    public String getMcDomain() {
        return mcDomain;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getNotificationUser() {
        return notificationUser;
    }

    public String getNotificationPass() {
        return notificationPass;
    }

    public String getNotificationHost() {
        return notificationHost;
    }

    public String getNotificationPort() {
        return notificationPort;
    }

    public String getNotificationFromAddress() {
        return notificationFromAddress;
    }

    public String getNotificationAdminAddress() {
        return notificationAdminAddress;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbPort() {
        return dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public String getSwiftAccessKey() {
        return swiftAccessKey;
    }

    public String getSwiftSecretKey() {
        return swiftSecretKey;
    }

    public String getSwiftEndpoint() {
        return swiftEndpoint;
    }

    public String getSwiftSignerType() {
        return swiftSignerType;
    }
}
