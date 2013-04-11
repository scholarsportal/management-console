/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */

package org.duracloud.account.app.controller;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class ChangePasswordForm extends AnonymousChangePasswordForm {

    @NotBlank(message="The old password field must contain a value.")
    private String oldPassword;
    
    public String getOldPassword() {
        return oldPassword;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
