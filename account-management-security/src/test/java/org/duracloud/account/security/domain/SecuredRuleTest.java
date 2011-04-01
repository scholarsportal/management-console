/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.domain;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

/**
 * @author Andrew Woods
 *         Date: 4/1/11
 */
public class SecuredRuleTest {

    private final String role = "ROLE_USER";
    private final String scope = "any";

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
