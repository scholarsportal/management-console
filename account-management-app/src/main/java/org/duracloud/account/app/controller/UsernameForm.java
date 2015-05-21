/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
