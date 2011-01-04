/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.easymock.Capture;
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
    private static final int userId = 1;

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

        userService.setUserRights(acctId, userId, Role.ROLE_USER);

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

        rightsRepo.save(EasyMock.isA(AccountRights.class));
        EasyMock.expectLastCall().anyTimes();

        replayMocks();
    }

    @Test
    public void testSetUserRightsStartAsNull() throws Exception {
        userService = new DuracloudUserServiceImpl(repoMgr);
        testSetRole(null);
    }

    @Test
    public void testSetUserRightsStartAsUser() throws Exception {
        userService = new DuracloudUserServiceImpl(repoMgr);
        testSetRole(Role.ROLE_USER);
    }

    @Test
    public void testSetUserRightsStartAsAdmin() throws Exception {
        userService = new DuracloudUserServiceImpl(repoMgr);
        testSetRole(Role.ROLE_ADMIN);
    }

    @Test
    public void testSetUserRightsStartAsOwner() throws Exception {
        userService = new DuracloudUserServiceImpl(repoMgr);
        testSetRole(Role.ROLE_OWNER);
    }

    private void testSetRole(Role initialRole) throws Exception {
        Capture<AccountRights> capture = setUpSetRights(initialRole);
        testSetRole(initialRole, capture, Role.ROLE_USER);
        testSetRole(initialRole, capture, Role.ROLE_ADMIN);
        testSetRole(initialRole, capture, Role.ROLE_OWNER);
    }

    private Capture<AccountRights> setUpSetRights(Role initialRole)
        throws Exception {
        AccountRights startingRights = getRightsWithRole(initialRole);
        EasyMock.expect(rightsRepo.findByAccountIdAndUserId(EasyMock.anyInt(),
                                                            EasyMock.anyInt()))
            .andReturn(startingRights)
            .anyTimes();

        Capture<AccountRights> capturedRights = new Capture<AccountRights>();
        rightsRepo.save(EasyMock.capture(capturedRights));
        EasyMock.expectLastCall().anyTimes();

        replayMocks();
        return capturedRights;
    }

    private AccountRights getRightsWithRole(Role role) {
        Set<Role> roles = new HashSet<Role>();
        if(null != role) {
            roles.add(Role.ROLE_USER);
            if(role.equals(Role.ROLE_ADMIN)) {
                roles.add(Role.ROLE_ADMIN);
            } else if(role.equals(Role.ROLE_OWNER)) {
                roles.add(Role.ROLE_ADMIN);
                roles.add(Role.ROLE_OWNER);
            }
        }
        return new AccountRights(0, acctId, userId, roles);
    }

    private void testSetRole(Role initialRole,
                             Capture<AccountRights> capture,
                             Role finalRole)
        throws Exception {
        userService.setUserRights(acctId, userId, finalRole);
        if(finalRole.equals(initialRole)) {
            try {
                capture.getValue();
                Assert.fail("Exception expected");
            } catch(AssertionError expected) {
                // If the initial role and final role are the same, no updates
                // should have been saved to the rights repo, therefore no
                // value should have been captured in that call.
            }
        } else {
            verifyFinalRights(capture.getValue(), finalRole.getRightsLevel());
        }
    }    

    private void verifyFinalRights(AccountRights finalRights, int numRoles) {
        Assert.assertNotNull(finalRights);
        Set<Role> finalRoles = finalRights.getRoles();
        Assert.assertNotNull(finalRoles);
        Assert.assertEquals(numRoles, finalRoles.size());
    }

    @Test
    public void testRedeemAccountInvitation() throws Exception {
        String redemptionCode = "ABCD";
        setUpRedeemAccountInvitation(redemptionCode);
        userService = new DuracloudUserServiceImpl(repoMgr);

        userService.redeemAccountInvitation(userId, redemptionCode);
    }

    private void setUpRedeemAccountInvitation(String redemptionCode)
        throws Exception {
        int invitationId = 0;
        int expirationDays = 2;
        UserInvitation invitation = new UserInvitation(invitationId,
                                                       acctId,
                                                       "my@email.com",
                                                       expirationDays,
                                                       redemptionCode);
        EasyMock.expect(
            invitationRepo.findByRedemptionCode(EasyMock.isA(String.class)))
            .andReturn(invitation)
            .anyTimes();

        EasyMock.expect(rightsRepo.findByAccountIdAndUserId(EasyMock.anyInt(),
                                                            EasyMock.anyInt()))
            .andThrow(new DBNotFoundException("No rights found"))
            .anyTimes();

        EasyMock.expect(idUtil.newRightsId())
            .andReturn(0)
            .anyTimes();

        rightsRepo.save(EasyMock.isA(AccountRights.class));
        EasyMock.expectLastCall().anyTimes();

        invitationRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();

        replayMocks();
    }
    
    @Test
    public void testRevokeUserRights() throws Exception {
        userService = new DuracloudUserServiceImpl(repoMgr);
        setUpRevokeUserRights();
        userService.revokeUserRights(acctId, userId);
    }

    private void setUpRevokeUserRights() throws Exception {
        AccountRights rights = new AccountRights(0, acctId, userId, null);
        EasyMock.expect(rightsRepo.findByAccountIdAndUserId(EasyMock.anyInt(),
                                                            EasyMock.anyInt()))
            .andReturn(rights)
            .anyTimes();

        rightsRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();

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
        userService = new DuracloudUserServiceImpl(repoMgr);
        setUpLoadDuracloudUserByUsername();
        userService.loadDuracloudUserByUsername("name");
    }

    private void setUpLoadDuracloudUserByUsername() throws Exception {
        DuracloudUser user = newDuracloudUser(userId, "some-username");
        EasyMock.expect(userRepo.findByUsername(EasyMock.isA(String.class)))
            .andReturn(user)
            .anyTimes();
        replayMocks();
    }

}
