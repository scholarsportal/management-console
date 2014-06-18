/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */

package org.duracloud.account.db.util.notification;

/**
 * @author: Bill Branan
 * Date: 12/8/11
 */
public class NotificationMgrConfig {

    private String fromAddress;
    private String username;
    private String password;
    private String adminAddress;

    public NotificationMgrConfig(String fromAddress, String username,
                                 String password, String adminAddress) {
        this.fromAddress = fromAddress;
        this.username = username;
        this.password = password;
        this.adminAddress = adminAddress;
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
