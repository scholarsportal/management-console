/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.annotation.UniqueUsernameConstraint;
import org.duracloud.common.annotation.FieldMatch;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
@FieldMatch(first = "password", second = "passwordConfirm", message = "The password fields must match")
public class NewUserForm {
	@Length(min=6, max=20)
	@UniqueUsernameConstraint
	private String username;

	@NotEmpty
	private String password; 

	@NotEmpty
	private String passwordConfirm; 

	@NotEmpty
	private String firstName; 

	@NotEmpty
	private String lastName;
	
	@Email
	@NotEmpty
	private String email; 
	
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
}
