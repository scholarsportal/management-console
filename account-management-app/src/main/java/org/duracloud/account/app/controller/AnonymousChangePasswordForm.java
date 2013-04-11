/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */

package org.duracloud.account.app.controller;

import org.duracloud.common.annotation.FieldMatch;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
@FieldMatch(first="password", second="passwordConfirm", message="The passwords do not match.")
public class AnonymousChangePasswordForm {
    @NotBlank(message = "Password must be specified.")
    private String password;
    private String passwordConfirm;
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPasswordConfirm() {
        return passwordConfirm;
    }
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
