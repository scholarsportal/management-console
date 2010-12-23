/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.annotation.UniqueUsernameConstraint;
import org.duracloud.common.annotation.FieldMatch;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
@FieldMatch(first="password", second="passwordConfirm", message="Password confirmation does not match.")
public class NewUserForm {
	@Length(min = 6, max = 20, message="Username must be between 6 and 20 characters in length.")
	@UniqueUsernameConstraint(message="Username is not available; please choose another.")
	private String username;

	@NotEmpty(message = "Password must be specified.")
	private String password;

	private String passwordConfirm;

	@NotBlank (message = "First name is empty.")
	private String firstName;

	@NotBlank (message = "Last name is empty.")
	private String lastName;

	@Email(message = "Email is invalid.")
	private String email;

	private String redemptionCode;
	
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

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

    public void setRedemptionCode(String redemptionCode) {
        this.redemptionCode = redemptionCode;
    }

    public String getRedemptionCode() {
        return redemptionCode;
    }
}
