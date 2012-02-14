/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.annotation.UsernameExists;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Daniel Bernstein
 *         Date: Feb 8, 2012
 * 
 */
public class UsernameForm {
    @UsernameExists
    @NotBlank(message = "A username must be specified.")
    private String username = null;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
