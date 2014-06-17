/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.domain;

import org.duracloud.account.db.model.Role;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 4/1/11
 */
public class SecuredRuleTest {

    private final Role role = Role.ROLE_USER;
    private final SecuredRule.Scope scope = SecuredRule.Scope.ANY;

    @Test
    public void testNull() {
        verifyRule(null, false);
    }

    @Test
    public void testCount() {
        verifyRule("x y", false);
    }

    @Test
    public void testPrefix() {
        verifyRule("role:" + role + ", x:" + scope, false);
        verifyRule("x:" + role + ", scope:" + scope, false);
    }

    @Test
    public void testValid() {
        SecuredRule rule = verifyRule("role:" + role + ", scope:" + scope,
                                      true);
        Assert.assertNotNull(rule);

        Assert.assertEquals(role, rule.getRole());
        Assert.assertEquals(scope, rule.getScope());
    }

    private SecuredRule verifyRule(String text, boolean isValid) {
        SecuredRule rule = null;
        boolean valid = true;
        try {
            rule = new SecuredRule(text);
        } catch (Exception e) {
            valid = false;
        }

        Assert.assertEquals(isValid, valid);
        return rule;
    }
}
