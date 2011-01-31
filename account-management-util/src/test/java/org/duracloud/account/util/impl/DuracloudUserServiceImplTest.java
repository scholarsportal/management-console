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

    @Test
    public void testSetUserRightsStartAsRoot() throws Exception {
        userService = new DuracloudUserServiceImpl(repoMgr);
        testSetRole(Role.ROLE_ROOT);
    }

        @Test
    public void testSetUserRightsStartAsInit() throws Exception {
        userService = new DuracloudUserServiceImpl(repoMgr);
        testSetRole(Role.ROLE_INIT);
    }

    @Test
    public void testSetUserRightsStartAsMulti() throws Exception {
        userService = new DuracloudUserServiceImpl(repoMgr);
        testSetRole(Role.ROLE_INIT, Role.ROLE_ADMIN);
    }

    private void testSetRole(Role... initialRoles) throws Exception {
        Capture<AccountRights> capture = setUpSetRights(initialRoles);
        testSetRole(initialRoles, capture, Role.ROLE_USER);
        testSetRole(initialRoles, capture, Role.ROLE_ADMIN);
        testSetRole(initialRoles, capture, Role.ROLE_OWNER);
        testSetRole(initialRoles, capture, Role.ROLE_ROOT);
        testSetRole(initialRoles, capture, Role.ROLE_INIT);
        testSetRole(initialRoles, capture, Role.ROLE_INIT, Role.ROLE_USER);
        testSetRole(initialRoles, capture, Role.ROLE_INIT, Role.ROLE_ADMIN);
        testSetRole(initialRoles, capture, Role.ROLE_INIT, Role.ROLE_OWNER);
        testSetRole(initialRoles, capture, Role.ROLE_INIT, Role.ROLE_ROOT);
    }

    private Capture<AccountRights> setUpSetRights(Role... initialRoles)
        throws Exception {
        AccountRights startingRights = getRightsWithRoles(initialRoles);
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

    private AccountRights getRightsWithRoles(Role... roles) {
        Set<Role> rolesWithHierarchy = new HashSet<Role>();
        if(null != roles) {
            for (Role role : roles) {
                if (null != role) {
                    rolesWithHierarchy.addAll(role.getRoleHierarchy());
                }
            }
        }
        return new AccountRights(0, acctId, userId, rolesWithHierarchy);
    }

    private void testSetRole(Role[] initialRoles,
                             Capture<AccountRights> capture,
                             Role... finalRoles)
        throws Exception {
        userService.setUserRights(acctId, userId, finalRoles);
        
        if(rolesAreSame(initialRoles, finalRoles)) {
            try {
                capture.getValue();
                Assert.fail("Exception expected");
            } catch(AssertionError expected) {
                // If the initial role and final role are the same, no updates
                // should have been saved to the rights repo, therefore no
                // value should have been captured in that call.
            }
        } else {
            Set<Role> newRoles = new HashSet<Role>();
            for (Role role : finalRoles) {
                newRoles.addAll(role.getRoleHierarchy());
            }
            verifyFinalRights(capture.getValue(), newRoles);
        }
    }

    private boolean rolesAreSame(Role[] initialRoles, Role[] finalRoles) {
        Set<Role> oldRoles = new HashSet<Role>();
        if (null != initialRoles) {
            for (Role role : initialRoles) {
                oldRoles.add(role);
            }
        }

        Set<Role> newRoles = new HashSet<Role>();
        if (null != finalRoles) {
            for (Role role : finalRoles) {
                newRoles.add(role);
            }
        }

        return oldRoles.equals(newRoles);
    }

    private void verifyFinalRights(AccountRights finalRights, Set<Role> expectedRoles) {
        Assert.assertNotNull(finalRights);
        Set<Role> finalRoles = finalRights.getRoles();
        Assert.assertNotNull(finalRoles);
        Assert.assertEquals(expectedRoles.size(), finalRoles.size());
        Assert.assertEquals(expectedRoles, finalRoles);
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
