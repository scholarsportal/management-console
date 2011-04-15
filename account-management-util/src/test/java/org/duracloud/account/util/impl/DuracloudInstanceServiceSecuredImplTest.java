/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.AccessDeniedException;
import org.duracloud.account.util.security.AnnotationParser;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: 4/11/11
 */
public class DuracloudInstanceServiceSecuredImplTest {

    private DuracloudInstanceServiceSecuredImpl instanceService;

    private DuracloudInstanceService wrappedInstanceService;
    private Authentication authentication;
    private AccessDecisionVoter voter;
    private AnnotationParser annotationParser;

    @Before
    public void setUp() throws Exception {
        wrappedInstanceService = EasyMock.createMock("DuracloudInstanceService",
                                                     DuracloudInstanceService.class);
        authentication = EasyMock.createMock("Authentication",
                                             Authentication.class);
        voter = EasyMock.createMock("AccessDecisionVoter",
                                    AccessDecisionVoter.class);
        annotationParser = EasyMock.createMock("AnnotationParser",
                                               AnnotationParser.class);

        // set up annotationParser
        Map<String, Object[]> methodMap = EasyMock.createMock("Map", Map.class);
        String[] annotationArgs = new String[]{"xx"};
        EasyMock.expect(methodMap.get(EasyMock.<Object>anyObject())).andReturn(
            annotationArgs);
        EasyMock.replay(methodMap);

        EasyMock.expect(annotationParser.getMethodAnnotationsForClass(Secured.class,
                                                                      DuracloudInstanceServiceSecuredImpl.class))
            .andReturn(methodMap);
        EasyMock.replay(annotationParser);

        instanceService = new DuracloudInstanceServiceSecuredImpl(
            wrappedInstanceService,
            authentication,
            voter,
            annotationParser);
    }

    private void setMockVoterExpectations(int access) {
        EasyMock.expect(voter.vote(EasyMock.isA(Authentication.class),
                                   EasyMock.isA(MethodInvocation.class),
                                   EasyMock.isA(Collection.class))).andReturn(
            access);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(wrappedInstanceService,
                        authentication,
                        voter,
                        annotationParser);
    }

    private void replayMocks() {
        EasyMock.replay(wrappedInstanceService, authentication, voter);
    }

    @Test
    public void testGetAccountId() throws Exception {
        EasyMock.expect(wrappedInstanceService.getAccountId()).andReturn(-1);
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        instanceService.getAccountId();
    }

    @Test
    public void testGetAccountIdFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            instanceService.getAccountId();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetInstanceInfo() throws Exception {
        EasyMock.expect(wrappedInstanceService.getInstanceInfo())
            .andReturn(null);

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        instanceService.getInstanceInfo();
    }

    @Test
    public void testGetInstanceVersion() throws Exception {
        EasyMock.expect(wrappedInstanceService.getInstanceVersion())
            .andReturn(null);

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        instanceService.getInstanceVersion();
    }

    @Test
    public void testGetInstanceInfoFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_ABSTAIN);
        replayMocks();

        boolean thrown = false;
        try {
            instanceService.getInstanceInfo();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetStatus() throws Exception {
        EasyMock.expect(wrappedInstanceService.getStatus()).andReturn(null);

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        instanceService.getStatus();
    }

    @Test
    public void testGetStatusFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            instanceService.getStatus();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testStop() throws Exception {
        wrappedInstanceService.stop();
        EasyMock.expectLastCall();

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        instanceService.stop();
    }

    @Test
    public void testStopFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            instanceService.stop();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testRestart() throws Exception {
        wrappedInstanceService.restart();
        EasyMock.expectLastCall();

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        instanceService.restart();
    }

    @Test
    public void testRestartFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_ABSTAIN);
        replayMocks();

        boolean thrown = false;
        try {
            instanceService.restart();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testInitialize() throws Exception {
        wrappedInstanceService.initialize();
        EasyMock.expectLastCall();

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        instanceService.initialize();
    }

    @Test
    public void testInitializeFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            instanceService.initialize();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testSetUserRoles() throws Exception {
        wrappedInstanceService.setUserRoles(null);
        EasyMock.expectLastCall();

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        instanceService.setUserRoles(null);
    }

    @Test
    public void testSetUserRolesFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            instanceService.setUserRoles(null);
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

}
