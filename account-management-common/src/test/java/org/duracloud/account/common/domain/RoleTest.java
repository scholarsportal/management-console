/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import junit.framework.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: Jan 3, 2011
 */
public class RoleTest {

    @Test
    public void testDisplayNames() {
        Assert.assertNotSame(Role.ROLE_ROOT.getDisplayName(),
                             Role.ROLE_OWNER.getDisplayName());
        Assert.assertNotSame(Role.ROLE_OWNER.getDisplayName(),
                             Role.ROLE_ADMIN.getDisplayName());
        Assert.assertNotSame(Role.ROLE_ADMIN.getDisplayName(),
                             Role.ROLE_USER.getDisplayName());
    }

    @Test
    public void testHierarchy() {
        // test root
        Set<Role> expectedHierarchy = new HashSet<Role>();
        expectedHierarchy.add(Role.ROLE_ROOT);
        expectedHierarchy.add(Role.ROLE_OWNER);
        expectedHierarchy.add(Role.ROLE_ADMIN);
        expectedHierarchy.add(Role.ROLE_USER);
        expectedHierarchy.add(Role.ROLE_ANONYMOUS);

        verifyHierarchy(expectedHierarchy, Role.ROLE_ROOT.getRoleHierarchy());

        // test owner
        expectedHierarchy = new HashSet<Role>();
        expectedHierarchy.add(Role.ROLE_OWNER);
        expectedHierarchy.add(Role.ROLE_ADMIN);
        expectedHierarchy.add(Role.ROLE_USER);
        expectedHierarchy.add(Role.ROLE_ANONYMOUS);

        verifyHierarchy(expectedHierarchy, Role.ROLE_OWNER.getRoleHierarchy());

        // test admin
        expectedHierarchy = new HashSet<Role>();
        expectedHierarchy.add(Role.ROLE_ADMIN);
        expectedHierarchy.add(Role.ROLE_USER);
        expectedHierarchy.add(Role.ROLE_ANONYMOUS);

        verifyHierarchy(expectedHierarchy, Role.ROLE_ADMIN.getRoleHierarchy());

        // test user
        expectedHierarchy = new HashSet<Role>();
        expectedHierarchy.add(Role.ROLE_USER);
        expectedHierarchy.add(Role.ROLE_ANONYMOUS);

        verifyHierarchy(expectedHierarchy, Role.ROLE_USER.getRoleHierarchy());

        // test init
        expectedHierarchy = new HashSet<Role>();
        expectedHierarchy.add(Role.ROLE_INIT);

        verifyHierarchy(expectedHierarchy, Role.ROLE_INIT.getRoleHierarchy());
    }

    private void verifyHierarchy(Set<Role> expectedHierarchy,
                                 Set<Role> hierarchy) {
        Assert.assertNotNull(hierarchy);
        Assert.assertTrue(hierarchy.size() > 0);
        Assert.assertEquals(expectedHierarchy.size(), hierarchy.size());
        Assert.assertEquals(expectedHierarchy, hierarchy);
    }

    @Test
    public void testHighestRole() {
        verifyHighestRole(Role.ROLE_ROOT);
        verifyHighestRole(Role.ROLE_OWNER);
        verifyHighestRole(Role.ROLE_ADMIN);
        verifyHighestRole(Role.ROLE_USER);
        verifyHighestRole(Role.ROLE_ANONYMOUS);
        verifyHighestRole(Role.ROLE_INIT);
    }

    private void verifyHighestRole(Role role) {
        Set<Role> roles = role.getRoleHierarchy();

        if (Role.ROLE_INIT == role) {
            Assert.assertEquals(null, Role.highestRole(roles));
        } else {
            Assert.assertEquals(role, Role.highestRole(roles));
        }
    }

}
