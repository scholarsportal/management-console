/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.notification;

import org.duracloud.common.model.EmailerType;

/**
 * @author: Bill Branan
 * Date: 12/8/11
 */
public class NotificationMgrConfig {

    private EmailerType emailerType;
    private String fromAddress;
    private String username;
    private String password;
    private String adminAddress;

    public NotificationMgrConfig(EmailerType emailerType, String fromAddress, String username,
                                 String password, String adminAddress) {
        this.emailerType = emailerType;
        this.fromAddress = fromAddress;
        this.username = username;
        this.password = password;
        this.adminAddress = adminAddress;
    }

    public EmailerType getEmailerType() {
        return emailerType;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAdminAddress() {
        return adminAddress;
    }
}
