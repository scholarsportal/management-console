/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.security.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.StringTokenizer;

/**
 * @author Andrew Woods
 *         Date: 4/8/11
 */
public class MethodInvocationImplTest {

    private MethodInvocationImpl invocation;

    private Object obj = "a-string-object";
    private String methodName = "charAt";
    private Object[] args = new Object[]{2};

    @Before
    public void setUp() throws Exception {
        invocation = new MethodInvocationImpl(obj, methodName, args);
    }

    @Test
    public void testGetMethod() throws Exception {
        Method method = invocation.getMethod();
        Assert.assertNotNull(method);
        Assert.assertEquals(methodName, method.getName());
    }

    @Test
    public void testGetArguments() throws Exception {
        Object[] arguments = invocation.getArguments();
        Assert.assertNotNull(arguments);
        Assert.assertEquals(args.length, arguments.length);
        for (int i = 0; i < args.length; ++i) {
            Assert.assertEquals(args[i], arguments[i]);
        }
    }

    @Test
    public void testGetThis() throws Exception {
        Object o = invocation.getThis();
        Assert.assertNotNull(o);
        Assert.assertEquals(obj, o);
    }

    @Test
    public void testProceed() throws Exception {
        boolean thrown = false;
        try {
            invocation.proceed();
            Assert.fail("exception expected");
        } catch (Throwable throwable) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetStaticPart() throws Exception {
        boolean thrown = false;
        try {
            invocation.getStaticPart();
            Assert.fail("exception expected");
        } catch (Throwable throwable) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testBadMethod() {
        String badName = "noSuchMethod";
        boolean thrown = false;
        try {
            invocation = new MethodInvocationImpl(obj, badName, args);
            Assert.fail("exception expected");
        } catch (Throwable throwable) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }
}
