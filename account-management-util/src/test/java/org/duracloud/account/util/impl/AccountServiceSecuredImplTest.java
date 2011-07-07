/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.InvoicePaymentInfo;
import org.duracloud.account.common.domain.PaymentInfo;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.error.AccessDeniedException;
import org.duracloud.account.util.security.AnnotationParser;
import org.duracloud.notification.Emailer;
import org.duracloud.storage.domain.StorageProviderType;
import org.duracloud.storage.provider.StorageProvider;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: 4/8/11
 */
public class AccountServiceSecuredImplTest {

    private AccountServiceSecuredImpl acctServiceImpl;

    private AccountService acctService;
    private Authentication authentication;
    private AccessDecisionVoter voter;
    private AnnotationParser annotationParser;

    @Before
    public void setUp() throws Exception {
        acctService = EasyMock.createMock("AccountService",
                                          AccountService.class);
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
                                                                      AccountServiceSecuredImpl.class))
            .andReturn(methodMap);
        EasyMock.replay(annotationParser);


        acctServiceImpl = new AccountServiceSecuredImpl(acctService,
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
        EasyMock.verify(acctService, authentication, voter, annotationParser);
    }

    private void replayMocks() {
        EasyMock.replay(acctService, authentication, voter);
    }

    @Test
    public void testRetrieveAccountInfo() throws Exception {
        EasyMock.expect(acctService.retrieveAccountInfo()).andReturn(null);
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.retrieveAccountInfo();
    }

    @Test
    public void testRetrieveAccountInfoFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.retrieveAccountInfo();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testStoreAccountInfo() throws Exception {
        String acctName = "acct-name";
        String orgName = "org-name";
        String dept = "dept";

        acctService.storeAccountInfo(acctName, orgName, dept);
        EasyMock.expectLastCall();

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.storeAccountInfo(acctName, orgName, dept);
    }

    @Test
    public void testStoreAccountStatus() throws Exception {
        AccountInfo.AccountStatus status = AccountInfo.AccountStatus.INACTIVE;

        acctService.storeAccountStatus(status);
        EasyMock.expectLastCall();

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.storeAccountStatus(status);
    }

    @Test
    public void testStoreAccountInfoFail() throws Exception {
        String acctName = "acct-name";
        String orgName = "org-name";
        String dept = "dept";

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_ABSTAIN);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.storeAccountInfo(acctName, orgName, dept);
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testRetrievePaymentInfo() throws Exception {
        EasyMock.expect(acctService.retrievePaymentInfo()).andReturn(null);

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.retrievePaymentInfo();
    }

    @Test
    public void testRetrievePaymentInfoFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.retrievePaymentInfo();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testStorePaymentInfo() throws Exception {
        PaymentInfo info = new InvoicePaymentInfo();
        acctService.storePaymentInfo(info);
        EasyMock.expectLastCall();

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.storePaymentInfo(info);
    }

    @Test
    public void testStorePaymentInfoFail() throws Exception {
        PaymentInfo info = new InvoicePaymentInfo();
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.storePaymentInfo(info);
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testStoreSubdomain() throws Exception {
        String subdomain = "subdomain";
        acctService.storeSubdomain(subdomain);
        EasyMock.expectLastCall();

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.storeSubdomain(subdomain);
    }

    @Test
    public void testStoreSubdomainFail() throws Exception {
        String subdomain = "subdomain";
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_ABSTAIN);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.storeSubdomain(subdomain);
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetSubdomain() throws Exception {
        String subdomain = "subdomain";
        EasyMock.expect(acctService.getSubdomain()).andReturn(subdomain);

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        Assert.assertEquals(subdomain, acctServiceImpl.getSubdomain());
    }

    @Test
    public void testGetSubdomainFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.getSubdomain();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetPrimaryStorageProvider() throws Exception {
        EasyMock.expect(acctService.getPrimaryStorageProvider())
            .andReturn(null);

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.getPrimaryStorageProvider();
    }

    @Test
    public void testGetPrimaryStorageProviderFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.getPrimaryStorageProvider();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetSecondaryStorageProviders() throws Exception {
        EasyMock.expect(acctService.getSecondaryStorageProviders()).andReturn(
            null);

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.getSecondaryStorageProviders();
    }

    @Test
    public void testGetSecondaryStorageProvidersFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.getSecondaryStorageProviders();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testAddStorageProvider() throws Exception {
        acctService.addStorageProvider(StorageProviderType.EMC);
        EasyMock.expectLastCall();

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.addStorageProvider(StorageProviderType.EMC);
    }

    @Test
    public void testAddStorageProviderFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.addStorageProvider(StorageProviderType.EMC);
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testRemoveStorageProvider() throws Exception {
        int id = 3;
        acctService.removeStorageProvider(id);
        EasyMock.expectLastCall();

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.removeStorageProvider(id);
    }

    @Test
    public void testRemoveStorageProviderFail() throws Exception {
        int id = 3;
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.removeStorageProvider(id);
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetUsers() throws Exception {
        EasyMock.expect(acctService.getUsers()).andReturn(null);

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.getUsers();
    }

    @Test
    public void testGetUsersFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.getUsers();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testInviteUser() throws Exception {
        String emailAddr = "x@y.com";
        String adminUsername = "test";
        Emailer emailer = EasyMock.createMock(Emailer.class);

        EasyMock.expect(acctService.inviteUser(emailAddr, adminUsername, emailer)).andReturn(
            null);

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.inviteUser(emailAddr, adminUsername, emailer);
    }

    @Test
    public void testInviteUserFail() throws Exception {
        String emailAddr = "x@y.com";
        String adminUsername = "test";
        Emailer emailer = EasyMock.createMock(Emailer.class);
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.inviteUser(emailAddr, adminUsername, emailer);
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetPendingInvitations() throws Exception {
        EasyMock.expect(acctService.getPendingInvitations()).andReturn(null);

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.getPendingInvitations();
    }

    @Test
    public void testGetPendingInvitationsFail() throws Exception {
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.getPendingInvitations();
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testDeleteUserInvitation() throws Exception {
        int id = 5;
        acctService.deleteUserInvitation(id);
        EasyMock.expectLastCall();

        setMockVoterExpectations(AccessDecisionVoter.ACCESS_GRANTED);
        replayMocks();

        acctServiceImpl.deleteUserInvitation(id);
    }

    @Test
    public void testDeleteUserInvitationFail() throws Exception {
        int id = 5;
        setMockVoterExpectations(AccessDecisionVoter.ACCESS_DENIED);
        replayMocks();

        boolean thrown = false;
        try {
            acctServiceImpl.deleteUserInvitation(id);
            Assert.fail("exception expected");
        } catch (AccessDeniedException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }
}
