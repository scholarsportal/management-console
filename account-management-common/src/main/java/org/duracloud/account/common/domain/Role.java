package org.duracloud.account.common.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;


public enum Role {
	ROLE_SYSTEM, 
	ROLE_ROOT, 
	ROLE_OWNER,
	ROLE_ADMIN, 
	ROLE_USER;

	private GrantedAuthority authority;

	Role() {
		this.authority = new GrantedAuthorityImpl(name());
	}

	public GrantedAuthority authority() {
		return this.authority;
	}
}
