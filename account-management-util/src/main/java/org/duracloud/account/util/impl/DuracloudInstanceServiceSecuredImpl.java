/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.AccessDeniedException;
import org.duracloud.account.util.security.AnnotationParser;
import org.duracloud.account.util.security.impl.MethodInvocationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class wraps another DuracloudInstanceService implementation with
 * AccessDecisionVoter security checks based on the DuracloudInstanceService
 * annotations.
 *
 * @author Andrew Woods
 *         Date: 4/10/11
 */
public class DuracloudInstanceServiceSecuredImpl implements DuracloudInstanceService {

    private Logger log = LoggerFactory.getLogger(
        DuracloudInstanceServiceSecuredImpl.class);

    private DuracloudInstanceService instanceService;
    private Authentication authentication;
    private AccessDecisionVoter voter;

    private Map<String, Object[]> methodMap;

    public DuracloudInstanceServiceSecuredImpl(DuracloudInstanceService instanceService,
                                               Authentication authentication,
                                               AccessDecisionVoter voter,
                                               AnnotationParser annotationParser) {
        this.instanceService = instanceService;
        this.authentication = authentication;
        this.voter = voter;
        this.methodMap = annotationParser.getMethodAnnotationsForClass(Secured.class,
                                                                       this.getClass());
    }

    private void throwIfAccessDenied(Object... args) {
        String methodName = getCurrentMethodName();

        Set<ConfigAttribute> configAtts = new HashSet<ConfigAttribute>();
        for (Object obj : methodMap.get(methodName)) {
            configAtts.add(new SecurityConfig((String) obj));
        }

        MethodInvocation invocation = new MethodInvocationImpl(this,
                                                               methodName,
                                                               args);
        int decision = voter.vote(authentication, invocation, configAtts);
        if (decision != AccessDecisionVoter.ACCESS_GRANTED) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private String getCurrentMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    @Override
    public int getAccountId() {
        throwIfAccessDenied();
        return instanceService.getAccountId();
    }

    @Override
    public DuracloudInstance getInstanceInfo() {
        throwIfAccessDenied();
        return instanceService.getInstanceInfo();
    }

    @Override
    public String getInstanceVersion() {
        throwIfAccessDenied();
        return instanceService.getInstanceVersion();
    }

    @Override
    public String getStatus() {
        throwIfAccessDenied();
        return instanceService.getStatus();
    }

    @Override
    public void stop() {
        throwIfAccessDenied();
        instanceService.stop();
    }

    @Override
    public void restart() {
        throwIfAccessDenied();
        instanceService.restart();
    }

    @Override
    public void initialize() {
        throwIfAccessDenied();
        instanceService.initialize();
    }

    @Override
    public void setUserRoles(Set<DuracloudUser> users) {
        throwIfAccessDenied(users);
        instanceService.setUserRoles(users);
    }
}
