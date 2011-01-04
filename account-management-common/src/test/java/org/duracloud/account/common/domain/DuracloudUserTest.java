/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Andrew Woods
 *         Date: Oct 12, 2010
 */
public class DuracloudUserTest {

    private DuracloudUser user;
    private static final int userId = 0;
    private static final String email = "email";
    private static final String lastName = "last-name";
    private static final String firstName = "first-name";
    private static final String password = "password";
    private static final String username = "username";

    private static final int acctId0 = 0;
    private static final int acctId1 = 1;

    @Before
    public void setUp() throws Exception {
        Set<Role> roles0 = new HashSet<Role>();
        roles0.add(Role.ROLE_USER);
        AccountRights rights0 = new AccountRights(0, acctId0, userId, roles0, 0);

        Set<Role> roles1 = new HashSet<Role>();
        roles1.add(Role.ROLE_USER);
        roles1.add(Role.ROLE_ADMIN);
        roles1.add(Role.ROLE_OWNER);
        roles1.add(Role.ROLE_ROOT);
        AccountRights rights1 = new AccountRights(1, acctId1, userId, roles1, 0);

        Set<AccountRights> rights = new HashSet<AccountRights>();
        rights.add(rights0);
        rights.add(rights1);

        user = new DuracloudUser(userId,
                                 username,
                                 password,
                                 firstName,
                                 lastName,
                                 email,
                                 0);
        user.setAccountRights(rights);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetAuthorities() {
        Collection<GrantedAuthority> authorities = user.getAuthorities();
        assertFalse(authorities.isEmpty());

        boolean user = false;
        boolean admin = false;
        boolean owner = false;
        boolean root = false;
        boolean system = false;
        for(GrantedAuthority ga : authorities) {
            if(ga.getAuthority().equals(Role.ROLE_USER.name())) {
                user = true;
            } else if(ga.getAuthority().equals(Role.ROLE_ADMIN.name())) {
                admin = true;
            } else if(ga.getAuthority().equals(Role.ROLE_OWNER.name())) {
                owner = true;
            } else if(ga.getAuthority().equals(Role.ROLE_ROOT.name())) {
                root = true;
            }
        }
        assertTrue(user);
        assertTrue(admin);
        assertTrue(owner);
        assertTrue(root);
    }

    @Test
    public void testGetRolesByAccount() {
        Set<Role> roles = user.getRolesByAcct(acctId0);
        assertNotNull(roles);
        assertEquals(1, roles.size());
        checkRoles(roles, true, false, false, false);

        roles = user.getRolesByAcct(acctId1);
        assertNotNull(roles);
        assertEquals(4, roles.size());
        checkRoles(roles, true, true, true, true);
    }

    private void checkRoles(Set<Role> roles,
                            boolean expUser,
                            boolean expAdmin,
                            boolean expOwner,
                            boolean expRoot) {
        boolean user = false;
        boolean admin = false;
        boolean owner = false;
        boolean root = false;
        for(Role role : roles) {
            if(role.equals(Role.ROLE_USER)) {
                user = true;
            } else if(role.equals(Role.ROLE_ADMIN)) {
                admin = true;
            } else if(role.equals(Role.ROLE_OWNER)) {
                owner = true;
            } else if(role.equals(Role.ROLE_ROOT)) {
                root = true;
            }
        }
        assertEquals(expUser, user);
        assertEquals(expAdmin, admin);
        assertEquals(expOwner, owner);
        assertEquals(expRoot, root);
    }

}
