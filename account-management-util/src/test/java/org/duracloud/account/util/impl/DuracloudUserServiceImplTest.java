/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserServiceImplTest extends DuracloudServiceTestBase {

    private DuracloudUserServiceImpl userService;

    private static final int acctId = 1;

    @Test
    public void testIsUsernameAvailable() throws Exception {
        String existingName = "name-existing";
        String newName = "name-new";
        setUpIsUsernameAvailable(existingName, newName);
        userService = new DuracloudUserServiceImpl(repoMgr);

        Assert.assertFalse(userService.isUsernameAvailable(existingName));
        Assert.assertTrue(userService.isUsernameAvailable(newName));
    }

    private void setUpIsUsernameAvailable(String existingName, String newName)
        throws Exception {
        EasyMock.expect(userRepo.findByUsername(existingName)).andReturn(null);
        EasyMock.expect(userRepo.findByUsername(newName))
            .andThrow(new DBNotFoundException("canned-exception"));

        replayMocks();
    }

    @Test
    public void testCreateNewUser() throws Exception {
        String newName = "new-username";
        String existingName = "existing-username";
        setUpCreateNewUser(newName, existingName);
        userService = new DuracloudUserServiceImpl(repoMgr);

        String password = "password";
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "email";
        DuracloudUser user = userService.createNewUser(newName,
                                                       password,
                                                       firstName,
                                                       lastName,
                                                       email);
        Assert.assertNotNull(user);
        Assert.assertEquals(newName, user.getUsername());


        boolean thrown = false;
        try {
            userService.createNewUser(existingName,
                                      password,
                                      firstName,
                                      lastName,
                                      email);
            Assert.fail("exception expected");
        } catch (UserAlreadyExistsException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);

        EasyMock.verify(userRepo);
    }

    private void setUpCreateNewUser(String newName, String existingName)
        throws Exception {
        int userId = 2;
        EasyMock.expect(userRepo.findByUsername(newName))
            .andThrow(new DBNotFoundException("canned-exception"));
        EasyMock.expect(userRepo.findByUsername(existingName)).andReturn(
            newDuracloudUser(userId, newName));

        userRepo.save(EasyMock.isA(DuracloudUser.class));
        EasyMock.expectLastCall();

        EasyMock.expect(idUtil.newUserId()).andReturn(userId).times(2);

        replayMocks();
    }

    @Test
    public void testGrantUserRights() throws Exception {
        int userId = 7;
        DuracloudUser user = newDuracloudUser(userId, "some-username");
        setUpAddUserToAccount(user);
        userService = new DuracloudUserServiceImpl(repoMgr);

        Set<Role> roles = user.getRolesByAcct(acctId);
        Assert.assertNotNull(roles);
        Assert.assertTrue(!roles.contains(acctId));

        userService.grantUserRights(acctId, userId);

        // FIXME: not sure how this user functionality is intended to work - aw.
        //  roles = user.getRolesByAcct(acctId);
        //  Assert.assertNotNull(roles);
        //  Assert.assertTrue(roles.contains(acctId));
        //  Assert.assertTrue(roles.contains(Role.ROLE_USER.name()));
    }

    private void setUpAddUserToAccount(DuracloudUser user) throws Exception {
        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_USER);
        AccountRights rights = new AccountRights(0,
                                                 acctId,
                                                 user.getId(),
                                                 roles);
        EasyMock.expect(rightsRepo.findByAccountIdAndUserId(EasyMock.anyInt(),
                                                            EasyMock.anyInt()))
            .andReturn(rights)
            .anyTimes();

        replayMocks();
    }

    @Test
    public void testRemoveUserFromAccount() throws Exception {
        //TODO: complete test
        replayMocks();
    }

    @Test
    public void testGrantAdminRights() throws Exception {
        //TODO: complete test
        replayMocks();
    }

    @Test
    public void testRevokeAdminRights() throws Exception {
        // TODO: complete test
        replayMocks();
    }

    @Test
    public void testSendPasswordReminder() throws Exception {
        // TODO: complete test
        replayMocks();
    }

    @Test
    public void testChangePassword() throws Exception {
        // TODO: complete test
        replayMocks();
    }

    @Test
    public void testloadDuracloudUserByUsername() throws Exception {
        // TODO: complete test
        replayMocks();
    }

    @Test
    public void testGrantOwnerRights() throws Exception {
        // TODO: complete test
        replayMocks();
    }

    @Test
    public void testRevokeOwnerRights() throws Exception {
        // TODO: complete test
        replayMocks();
    }

}
