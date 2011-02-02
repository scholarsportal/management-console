/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import org.duracloud.account.common.domain.Role;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

/**
 * @author Andrew Woods
 *         Date: Jan 31, 2011
 */
public class InitUserDetailServiceTest {

    private InitUserDetailService initService = new InitUserDetailService();

    @Test
    public void testLoadUserByUsername() throws Exception {
        verifyLoadUser(null, false);
        verifyLoadUser("junk", false);
        verifyLoadUser("init", true);
    }

    private void verifyLoadUser(String username, boolean expected) {
        UserDetails userDetails = null;

        boolean success = true;
        try {
            userDetails = initService.loadUserByUsername(username);

        } catch (UsernameNotFoundException nnfe) {
            success = false;
        }
        Assert.assertEquals(expected, success);
        Assert.assertEquals(expected, null != userDetails);
    }

    @Test
    public void testLoadUserByUsernameResponse() throws Exception {
        UserDetails userDetails = initService.loadUserByUsername("init");
        Assert.assertNotNull(userDetails);

        Collection<GrantedAuthority> authorities = userDetails.getAuthorities();
        Assert.assertNotNull(authorities);

        Assert.assertEquals(2, authorities.size());
        boolean foundUser = false;
        boolean foundInit = false;
        for (GrantedAuthority authority : authorities) {

            String authName = authority.getAuthority();
            if (Role.ROLE_USER.name().equals(authName)) {
                foundUser = true;

            } else if (Role.ROLE_INIT.name().equals(authName)) {
                foundInit = true;

            } else {
                Assert.fail("Unexpected authority: " + authName);
            }
        }
        Assert.assertTrue("user role", foundUser);
        Assert.assertTrue("init role", foundInit);

    }
}
