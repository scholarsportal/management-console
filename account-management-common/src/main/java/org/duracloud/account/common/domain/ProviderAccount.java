/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

/**
 * @author: Bill Branan
 * Date: 3/24/11
 */
public abstract class ProviderAccount extends BaseDomainData {

    /**
     * The username necessary to connect to this provider's services. This may
     * have different names at each provider (e.g. at Amazon, this is the
     * Access Key ID)
     */
    protected String username;

    /**
     * The password necessary to connect to this provider's services. This may
     * have different names at each provider (e.g. at Amazon, this is the
     * Secret Access Key)
     */
    protected String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
