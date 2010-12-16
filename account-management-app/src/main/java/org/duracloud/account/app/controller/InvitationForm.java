/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.Role;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class InvitationForm {
    private String emailAddresses = null;
    private Role role = Role.ROLE_USER;
    private static Role[] ADMIN_ROLES =
        new Role[] { Role.ROLE_USER, Role.ROLE_ADMIN };
    private static Role[] OWNER_ROLES =
        new Role[] { Role.ROLE_USER, Role.ROLE_ADMIN, Role.ROLE_OWNER };

    public void setEmailAddresses(String emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    public String getEmailAddresses() {
        return emailAddresses;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }
    
    public Role[] getAdminRoles(){
        return ADMIN_ROLES;
    }
    
    public Role[] getOwnerRoles(){
        return OWNER_ROLES;
    }
}
