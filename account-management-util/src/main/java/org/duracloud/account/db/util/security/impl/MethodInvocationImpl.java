/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.security.impl;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * This class provides an implementation of the aopalliance MethodInvocation
 * for use in AccessDecisionVoter security checks in cases where the
 * application context is not managing the security AOP.
 *
 * @author Andrew Woods
 *         Date: 4/8/11
 */
public class MethodInvocationImpl implements MethodInvocation {

    private Logger log = LoggerFactory.getLogger(MethodInvocationImpl.class);

    private Object obj;
    private Method method;
    private Object[] args;

    public MethodInvocationImpl(Object obj, String methodName, Object[] args) {
        this.obj = obj;
        this.args = args;
        for (Method m : obj.getClass().getDeclaredMethods()) {
            if (methodName.equals(m.getName())) {
                this.method = m;
            }
        }

        if (null == this.method) {
            String msg = "Method not found on class: " + methodName + ", " +
                obj.getClass().getName();
            log.error(msg);
            throw new DuraCloudRuntimeException(msg);
        }
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return args;
    }

    @Override
    public Object getThis() {
        return obj;
    }

    @Override
    public Object proceed() throws Throwable {
        throw new UnsupportedOperationException();
    }

    @Override
    public AccessibleObject getStaticPart() {
        throw new UnsupportedOperationException();
    }
}
