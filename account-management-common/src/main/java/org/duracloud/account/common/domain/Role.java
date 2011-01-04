/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;


public enum Role {
	ROLE_ROOT(4, "Root"),
	ROLE_OWNER(3, "Owner"),
	ROLE_ADMIN(2, "Administrator"),
	ROLE_USER(1, "User");

	private GrantedAuthority authority;
    private String displayName;
    private int rightsLevel;

	Role(int rightsLevel, String displayName) {
		this.authority = new GrantedAuthorityImpl(name());
        this.rightsLevel = rightsLevel;
        this.displayName = displayName;
	}

	public GrantedAuthority authority() {
		return this.authority;
	}

    public String getDisplayName() {
        return displayName;
    }

    public int getRightsLevel() {
        return rightsLevel;
    }        
}
