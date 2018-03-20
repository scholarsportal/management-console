/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.annotation.EmailAddressesConstraint;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class InvitationForm {
    @EmailAddressesConstraint
    @NotBlank(message = "Email address must be specified.")
    private String emailAddresses = null;

    public void setEmailAddresses(String emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public String getEmailAddresses() {
        return emailAddresses;
    }
}
