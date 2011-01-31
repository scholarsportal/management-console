/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.util.HashSet;
import java.util.Set;


public enum Role {
    ROLE_INIT("Init"),
    ROLE_ROOT("Root"),
    ROLE_OWNER("Owner"),
    ROLE_ADMIN("Administrator"),
    ROLE_USER("User");

    private GrantedAuthority authority;
    private String displayName;

    Role(String displayName) {
        this.authority = new GrantedAuthorityImpl(name());
        this.displayName = displayName;
    }

    public GrantedAuthority authority() {
        return this.authority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<Role> getRoleHierarchy() {
        Set<Role> hierarchy = new HashSet<Role>();
        switch (this) {
            case ROLE_ROOT:
                hierarchy.add(ROLE_ROOT);
            case ROLE_OWNER:
                hierarchy.add(ROLE_OWNER);
            case ROLE_ADMIN:
                hierarchy.add(ROLE_ADMIN);
            case ROLE_USER:
                hierarchy.add(ROLE_USER);
                break;
            case ROLE_INIT:
                // not in hierarchy
                hierarchy.add(ROLE_INIT);
        }

        return hierarchy;
    }
}
