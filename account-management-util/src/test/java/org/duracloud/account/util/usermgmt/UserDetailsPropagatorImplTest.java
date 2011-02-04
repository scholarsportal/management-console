/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.usermgmt;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.util.impl.DuracloudInstanceServiceImpl;
import org.duracloud.account.util.impl.DuracloudServiceTestBase;
import org.duracloud.account.util.usermgmt.impl.UserDetailsPropagatorImpl;
import org.duracloud.client.ContentStore;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Feb 3, 2011
 */
public class UserDetailsPropagatorImplTest {

    private UserDetailsPropagator propagator;

    private DuracloudInstanceManagerService instanceManagerService;
    private AccountManagerService accountManagerService;
    private AccountService accountService;

    private static final int NUM_INSTANCES = 2;
    private Set<DuracloudInstanceService> instanceServices;

    private int acctId = 2;
    private int userId = 3;
    private Set<Role> roles;
    private Set<Role> newRoles;

    private static final int NUM_USERS = 4;
    private Set<DuracloudUser> users;

    @Before
    public void setUp() throws Exception {

        instanceServices = new HashSet<DuracloudInstanceService>();

        roles = new HashSet<Role>();
        roles.add(Role.ROLE_ADMIN);
        roles.add(Role.ROLE_USER);

        newRoles = new HashSet<Role>();
        newRoles.add(Role.ROLE_OWNER);

        users = new HashSet<DuracloudUser>();
        for (int i = 0; i < NUM_USERS; ++i) {
            users.add(newDuracloudUser(i, "user-" + i));
        }
    }

    private void replayMocks() {
        EasyMock.replay(instanceManagerService);
        EasyMock.replay(accountManagerService);
        EasyMock.replay(accountService);

        for (DuracloudInstanceService service : instanceServices) {
            EasyMock.replay(service);
        }
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(instanceManagerService);
        EasyMock.verify(accountManagerService);
        EasyMock.verify(accountService);

        for (DuracloudInstanceService service : instanceServices) {
            EasyMock.verify(service);
        }
    }

    @Test
    public void testPropagateRights() throws Exception {
        boolean revoke = false;
        createAccountManagerExpectation();
        createInstanceManagerExpectation(revoke);
        replayMocks();

        propagator = new UserDetailsPropagatorImpl(instanceManagerService,
                                                   accountManagerService);

        propagator.propagateRights(acctId, userId, newRoles);
    }

    @Test
    public void testPropagateRevocation() throws Exception {
        boolean revoke = true;
        createAccountManagerExpectation();
        createInstanceManagerExpectation(revoke);
        replayMocks();

        propagator = new UserDetailsPropagatorImpl(instanceManagerService,
                                                   accountManagerService);

        propagator.propagateRevocation(acctId, userId);
    }

    private void createAccountManagerExpectation()
        throws AccountNotFoundException {
        accountManagerService = EasyMock.createMock("AccountManagerService",
                                                    AccountManagerService.class);
        accountService = EasyMock.createMock("AccountService",
                                             AccountService.class);
        EasyMock.expect(accountService.getUsers()).andReturn(users);

        EasyMock.expect(accountManagerService.getAccount(acctId)).andReturn(
            accountService);
    }

    private void createInstanceManagerExpectation(boolean revoke)
        throws DuracloudInstanceNotAvailableException {
        instanceManagerService = EasyMock.createMock(
            "DuracloudInstanceManagerService",
            DuracloudInstanceManagerService.class);

        for (int i = 0; i < NUM_INSTANCES; ++i) {
            DuracloudInstanceService instance = EasyMock.createMock(
                "DuracloudInstanceService",
                DuracloudInstanceService.class);

            instance.setUserRoles(EasyMock.isA(Set.class));
            EasyMock.expectLastCall().andStubAnswer(verifyRoles(revoke));
            instanceServices.add(instance);
        }

        EasyMock.expect(instanceManagerService.getInstanceServices(acctId))
            .andReturn(instanceServices);
    }

    /**
     * This method verifies that the users passed into the call matches the
     * expected users/roles.
     * The return value is ignore, hence the null.
     *
     * @return not used
     */
    private IAnswer<? extends Object> verifyRoles(final boolean revoke) {
        return new IAnswer<Object>() {
            public Object answer() throws Throwable {
                Object[] args = EasyMock.getCurrentArguments();
                Assert.assertNotNull(args);
                Assert.assertEquals(1, args.length);

                Set<DuracloudUser> argUsers = (Set<DuracloudUser>) args[0];

                if (revoke) {
                    Assert.assertEquals(NUM_USERS - 1, argUsers.size());
                } else {
                    Assert.assertEquals(NUM_USERS, argUsers.size());
                }

                for (DuracloudUser user : argUsers) {
                    Set<Role> argRoles = user.getRolesByAcct(acctId);
                    Assert.assertNotNull(argRoles);

                    if (userId == user.getId()) {
                        Assert.assertEquals(1, argRoles.size());
                        Assert.assertTrue(argRoles.contains(Role.ROLE_OWNER));

                    } else {
                        Assert.assertEquals(2, argRoles.size());
                        Assert.assertTrue(!argRoles.contains(Role.ROLE_OWNER));
                    }
                }
                return null;
            }
        };
    }

    private DuracloudUser newDuracloudUser(int userId, String username) {
        String password = "password";
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "email";
        DuracloudUser user = new DuracloudUser(userId,
                                               username,
                                               password,
                                               firstName,
                                               lastName,
                                               email);

        Set<AccountRights> rightsSet = new HashSet<AccountRights>();
        rightsSet.add(new AccountRights(-1, acctId, userId, roles));
        user.setAccountRights(rightsSet);
        return user;
    }

}
