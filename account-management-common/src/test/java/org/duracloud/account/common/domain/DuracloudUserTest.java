/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.common.domain;

import java.util.List;
import java.util.Map;

import org.duracloud.account.security.Role;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: Oct 12, 2010
 */
public class DuracloudUserTest {

    private DuracloudUser user;
    private static final String email = "email";
    private static final String lastName = "last-name";
    private static final String firstName = "first-name";
    private static final String password = "password";
    private static final String username = "username";

    private static final String acct0 = "acct-0";
    private static final String acct1 = "acct-1";

    @Before
    public void setUp() throws Exception {
        user = new DuracloudUser(username,
                                 password,
                                 firstName,
                                 lastName,
                                 email);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAddAccount() throws Exception {
        verifyAcctToRoles(user.getAcctToRoles(), 0, null);

        // add first acct
        user.addAccount(acct0);
        verifyAcctToRoles(user.getAcctToRoles(), 1, acct0, Role.ROLE_USER.name());
        
        // check idempotency
        user.addAccount(acct0);
        verifyAcctToRoles(user.getAcctToRoles(), 1, acct0, Role.ROLE_USER.name());

        // add another acct
        user.addAccount(acct1);
        verifyAcctToRoles(user.getAcctToRoles(), 2, acct0, Role.ROLE_USER.name());
        verifyAcctToRoles(user.getAcctToRoles(), 2, acct1, Role.ROLE_USER.name());
    }

    private void verifyAcctToRoles(Map<String, List<String>> acctToRoles,
                                   int expectedSize,
                                   String expectedAcct,
                                   String... expectedRoles) {
        Assert.assertNotNull(acctToRoles);
        Assert.assertEquals(expectedSize, acctToRoles.size());

        if (null != expectedAcct) {
            Assert.assertTrue(acctToRoles.containsKey(expectedAcct));
            List<String> roles = acctToRoles.get(expectedAcct);
            Assert.assertNotNull(roles);

            Assert.assertEquals(expectedRoles.length, roles.size());
            for (String expectedRole : expectedRoles) {
                Assert.assertTrue(roles.contains(expectedRole));
            }
        }
    }
}
