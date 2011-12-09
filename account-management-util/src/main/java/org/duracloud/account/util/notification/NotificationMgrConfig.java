/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util.notification;

/**
 * @author: Bill Branan
 * Date: 12/8/11
 */
public class NotificationMgrConfig {

    private String fromAddress;
    private String username;
    private String password;

    public NotificationMgrConfig(String fromAddress, String username,
                                 String password) {
        this.fromAddress = fromAddress;
        this.username = username;
        this.password = password;
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

}
