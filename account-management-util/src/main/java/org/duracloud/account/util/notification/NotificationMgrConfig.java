/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */

package org.duracloud.account.util.notification;

import java.util.Collection;

/**
 * @author: Bill Branan
 * Date: 12/8/11
 */
public class NotificationMgrConfig {

    private String fromAddress;
    private String username;
    private String password;
    private Collection<String> adminAddresses;

    public NotificationMgrConfig(String fromAddress, String username,
                                 String password, Collection<String> adminAddresses) {
        this.fromAddress = fromAddress;
        this.username = username;
        this.password = password;
        this.adminAddresses = adminAddresses;
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

    public Collection<String> getAdminAddresses() {
        return adminAddresses;
    }
}
