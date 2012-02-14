/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */

package org.duracloud.account.app.controller;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class UserProfileEditForm {
    @NotBlank(message="First name is required")
    private String firstName;

    @NotBlank(message="Last name is required")
    private String lastName;

    @NotEmpty(message = "Email must be specified.")
    @Email(message="Email is invalid")
    private String email;

	@NotBlank (message = "Security question is empty.")
	private String securityQuestion;

	@NotBlank (message = "Security answer is empty.")
	private String securityAnswer;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}
