/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author: Bill Branan
 * Date: 4/6/11
 */
public class AccountInstanceForm {
    @NotBlank(message = "You must specify a version.")
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
