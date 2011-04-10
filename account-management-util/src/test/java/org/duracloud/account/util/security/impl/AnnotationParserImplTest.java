/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.security.impl;

import org.duracloud.account.util.impl.AccountServiceSecuredImpl;
import org.duracloud.account.util.security.AnnotationParser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.annotation.Secured;

import java.lang.reflect.Method;
import java.text.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: 4/8/11
 */
public class AnnotationParserImplTest {

    private AnnotationParser parser;

    private Class annotationClass = Secured.class;
    private Class targetClass = AccountServiceSecuredImpl.class;

    @Before
    public void setUp() throws Exception {
        parser = new AnnotationParserImpl();
    }

    @Test
    public void testGetMethodAnnotationsForClass() throws Exception {
        Map<String, Object[]> methodAnnotations = parser.getMethodAnnotationsForClass(
            annotationClass,
            targetClass);

        Assert.assertNotNull(methodAnnotations);
        Assert.assertTrue(methodAnnotations.size() > 0);

        Method[] methods = targetClass.getInterfaces()[0].getDeclaredMethods();
        Assert.assertEquals(methods.length, methodAnnotations.size());

        for (Method method : methods) {
            Object[] values = methodAnnotations.get(method.getName());
            Assert.assertEquals(1, values.length);

            String val = (String) values[0];
            Assert.assertTrue(val, val.startsWith("role"));
        }
    }

    @Test
    public void testScopeCorrectOnGetAccountId() {
        Map<String, Object[]> methodAnnotations = parser.getMethodAnnotationsForClass(
            annotationClass,
            targetClass);

        Assert.assertNotNull(methodAnnotations);

        Object[] values = methodAnnotations.get("getAccountId");
        Assert.assertNotNull(values);

        Assert.assertEquals(1, values.length);

        String val = (String) values[0];
        Assert.assertEquals("role:ROLE_ANONYMOUS, scope:ANY", val);
        // The above security config needs to be of 'scope' ANY for the
        //  AccountAccessDecisionVoter to have access to the acctId of the
        //  target AccountInfo.
    }
}

