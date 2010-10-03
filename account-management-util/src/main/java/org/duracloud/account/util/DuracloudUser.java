package org.duracloud.account.util;

import org.duracloud.security.domain.SecurityUserBean;
/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class DuracloudUser extends SecurityUserBean{
	private String fullName;
	private String email;
	/**
	 * 
	 * @return
	 */
	public String getFullName() {
		return fullName;
	}
	/**
	 * 
	 * @param fullName
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	/**
	 * 
	 * @return
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * 
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
