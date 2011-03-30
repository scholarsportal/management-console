/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.hibernate.validator.constraints.Length;

public class ForgotPasswordForm {
	@Length(min = 4, max = 20, message="Username must be between 4 and 20 characters in length.")
	private String username;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
