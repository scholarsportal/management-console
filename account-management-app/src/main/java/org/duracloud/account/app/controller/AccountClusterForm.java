/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.hibernate.validator.constraints.NotBlank;


/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 27, 2012
 */
public class AccountClusterForm {
    @NotBlank(message="Please specify a name for the cluster.")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
