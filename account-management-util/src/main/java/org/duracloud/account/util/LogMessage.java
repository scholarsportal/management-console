/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

import java.util.Date;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class LogMessage {

	private Date createdDate;

	/**
	 * 
	 * @param username
	 *            required
	 * @param fullName
	 *            optional
	 * @param message
	 *            required
	 */
	public LogMessage(String username, String fullName, String message) {
		super();
		this.createdDate = new Date();
		if (username == null || message == null) {
			throw new NullPointerException(
					"username and message must be non null values");
		}
		this.username = username;
		this.fullName = fullName;
		this.message = message;
	}

	private String username;
	private String fullName;
	private String message;

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getUsername() {
		return username;
	}

	public String getFullName() {
		return fullName;
	}

	public String getMessage() {
		return message;
	}
}
