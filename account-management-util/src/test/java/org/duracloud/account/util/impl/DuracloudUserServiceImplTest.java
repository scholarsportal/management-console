/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.InitUserCredential;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.error.DBUninitializedException;
import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.duracloud.account.util.error.AccountRequiresOwnerException;
import org.duracloud.account.util.error.InvalidPasswordException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserServiceImplTest extends DuracloudServiceTestBase {

    private static final int acctId = 1;
    private static final int userId = 1;

    @Before
    @Override
    public void before() throws Exception {
        super.before();
        userService = new DuracloudUserServiceImpl(repoMgr, notificationMgr, propagator);
    }

    @Test
    public void testIsUsernameAvailable() throws Exception {
        String existingName = "name-existing";
        String newName = "name-new";
        setUpIsUsernameAvailable(existingName, newName);

        String rootName = ServerImage.DC_ROOT_USERNAME;
        String initName = new InitUserCredential().getUsername();
        Assert.assertFalse(userService.isUsernameAvailable(existingName));
        Assert.assertFalse(userService.isUsernameAvailable("root"));
        Assert.assertFalse(userService.isUsernameAvailable("RooT"));
        Assert.assertFalse(userService.isUsernameAvailable(rootName));
        Assert.assertFalse(userService.isUsernameAvailable(initName));
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

        String password = "password";
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "email";
        String securityQuestion = "question";
        String securityAnswer = "answer";
        DuracloudUser user = userService.createNewUser(newName,
                                                       password,
                                                       firstName,
                                                       lastName,
                                                       email,
                                                       securityQuestion,
                                                       securityAnswer);
        Assert.assertNotNull(user);
        Assert.assertEquals(newName, user.getUsername());


        boolean thrown = false;
        try {
            userService.createNewUser(existingName,
                                      password,
                                      firstName,
                                      lastName,
                                      email,
                                      securityQuestion,
                                      securityAnswer);
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
        Set<Role> roles = Role.ROLE_USER.getRoleHierarchy();
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
        testSetRole((Role)null);
    }

    @Test
    public void testSetUserRightsStartAsUser() throws Exception {
        testSetRole(Role.ROLE_USER);
    }

    @Test
    public void testSetUserRightsStartAsAdmin() throws Exception {
        testSetRole(Role.ROLE_ADMIN);
    }

    @Test
    public void testSetUserRightsStartAsOwnerFail() throws Exception {
        try {
            testSetRole(Role.ROLE_OWNER);
            Assert.fail("AccountRequiresOwnerException Expected");
        } catch(AccountRequiresOwnerException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testSetUserRightsStartAsOwner() throws Exception {
        testSetRole(2, Role.ROLE_OWNER);
    }

    @Test
    public void testSetUserRightsStartAsRoot() throws Exception {
        testSetRole(2, Role.ROLE_ROOT);
    }

    @Test
    public void testSetUserRightsStartAsInit() throws Exception {
        testSetRole(Role.ROLE_INIT);
    }

    @Test
    public void testSetUserRightsStartAsMulti() throws Exception {
        testSetRole(Role.ROLE_INIT, Role.ROLE_ADMIN);
    }

    private void testSetRole(Role... initialRoles) throws Exception {
        testSetRole(1, initialRoles);
    }

    private void testSetRole(int numAccounts,
                             Role... initialRoles) throws Exception {
        Capture<AccountRights> capture =
            setUpSetRights(numAccounts, initialRoles);
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

    private Capture<AccountRights> setUpSetRights(int numAccounts,
                                                  Role... initialRoles)
        throws Exception {
        Set<Role> initRoles = getRolesWithHierarchy(initialRoles);
        Set<AccountRights> rightsSet = new HashSet<AccountRights>();
        for(int i=1; i<=numAccounts; i++) {
            AccountRights rights =
                new AccountRights(i, acctId, i, initRoles);
            rightsSet.add(rights);
        }

        EasyMock.expect(rightsRepo.findByAccountId(EasyMock.anyInt()))
            .andReturn(rightsSet)
            .anyTimes();

        propagator.propagateRights(EasyMock.eq(acctId),
                                   EasyMock.eq(userId),
                                   EasyMock.isA(Set.class));
        EasyMock.expectLastCall()
            .anyTimes();

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
        Set<Role> rolesWithHierarchy = getRolesWithHierarchy(roles);
        return new AccountRights(0, acctId, userId, rolesWithHierarchy);
    }

    private Set<Role> getRolesWithHierarchy(Role[] roles) {
        Set<Role> rolesWithHierarchy = new HashSet<Role>();
        if(null != roles) {
            for (Role role : roles) {
                if (null != role) {
                    rolesWithHierarchy.addAll(role.getRoleHierarchy());
                }
            }
        }
        return rolesWithHierarchy;
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

        propagator.propagateRights(EasyMock.eq(acctId),
                                   EasyMock.eq(userId),
                                   EasyMock.isA(Set.class));
        EasyMock.expectLastCall()
            .times(1);

        replayMocks();
    }

    @Test
    public void testRevokeUserRights() throws Exception {
        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_USER);

        setUpRevokeUserRights(roles);
        userService.revokeUserRights(acctId, userId);
    }

    @Test
    public void testRevokeUserRightsOwner() throws Exception {
        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_USER);
        roles.add(Role.ROLE_ADMIN);
        roles.add(Role.ROLE_OWNER);

        setUpRevokeUserRights(roles);
        try {
            userService.revokeUserRights(acctId, userId);
            Assert.fail("AccountRequiresOwnerException Expected");
        } catch (AccountRequiresOwnerException e) {
            Assert.assertNotNull(e);
        }
    }

    private void setUpRevokeUserRights(Set<Role> roles) throws Exception {
        AccountRights rights = new AccountRights(0, acctId, userId, roles);
        Set<AccountRights> rightsSet = new HashSet<AccountRights>();
        rightsSet.add(rights);

        EasyMock.expect(rightsRepo.findByAccountId(EasyMock.anyInt()))
            .andReturn(rightsSet)
            .times(1);

        EasyMock.expect(rightsRepo.findByAccountIdAndUserId(EasyMock.anyInt(),
                                                            EasyMock.anyInt()))
            .andReturn(rights)
            .anyTimes();

        rightsRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();

        if(!roles.contains(Role.ROLE_OWNER)) {
            propagator.propagateRevocation(EasyMock.eq(acctId), EasyMock.eq(userId));
            EasyMock.expectLastCall()
                .times(1);
        }

        replayMocks();
    }

    @Test
    public void testSendPasswordReminder() throws Exception {
        // TODO: complete test
        replayMocks();
    }

    @Test
    public void testChangePasswordUser() throws Exception {
        String username = "test-username";
        String password = "test-newPassword";
        setUpChangePassword(username, true, true, Role.ROLE_USER);

        userService.changePassword(userId, "password", false, password);
    }

    @Test
    public void testChangePasswordRoot() throws Exception {
        String username = "root-username";
        String password = "root-newPassword";
        setUpChangePassword(username, true, false, Role.ROLE_ROOT);

        userService.changePassword(userId, "password", false, password);
    }

    @Test
    public void testChangeIncorrectPassword() throws Exception {
        Exception exception = new InvalidPasswordException(userId);

        String username = "test-username";
        String password = "test-newPassword";
        setUpChangePassword(username, false, false, Role.ROLE_USER);

        try {
            userService.changePassword(userId, "incorrect-password", false, password);
            Assert.fail("exception expected");
        } catch(InvalidPasswordException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testChangePasswordToSamePassword() throws Exception {
        replayMocks();
        String password = "password";
        userService.changePassword(userId, password, false, password);
    }

    private void setUpChangePassword(String username,
                                     boolean expectNoErrors,
                                     boolean expectPropagate,
                                     Role userRole)
        throws Exception {
        DuracloudUser user = newDuracloudUser(userId, username);

        EasyMock.expect(userRepo.findById(userId))
            .andReturn(user)
            .anyTimes();

        userRepo.save(user);
        EasyMock.expectLastCall().anyTimes();

        if(expectNoErrors) {
            EasyMock.expect(rightsRepo.findByUserId(userId))
                .andReturn(getRightsSet(userRole))
                .times(1);
        }

        if(expectPropagate) {
            propagator.propagatePasswordUpdate(acctId, userId);
            EasyMock.expectLastCall()
                .times(1);
        }

        replayMocks();
    }

    @Test
    public void testLoadDuracloudUserByUsername() throws Exception {
        String username = "test-username";
        setUpLoadDuracloudUserByUsername(username, null);

        DuracloudUser user = userService.loadDuracloudUserByUsername(username);
        Assert.assertNotNull(user);
        Assert.assertEquals(username, user.getUsername());

        Set<Role> roles = user.getRolesByAcct(acctId);
        Assert.assertNotNull(roles);
        Assert.assertEquals(2, roles.size());
        Assert.assertTrue(roles.contains(Role.ROLE_USER));
        Assert.assertTrue(roles.contains(Role.ROLE_ANONYMOUS));

        Collection<GrantedAuthority> authorities = user.getAuthorities();
        Assert.assertNotNull(authorities);
        Assert.assertEquals(2, authorities.size());

        Set<String> roleNames = new HashSet<String>();
        Iterator<GrantedAuthority> itr = authorities.iterator();
        while (itr.hasNext()) {
            roleNames.add(itr.next().getAuthority());
        }

        for (Role role : Role.ROLE_USER.getRoleHierarchy()) {
            Assert.assertTrue(roleNames.contains(role.name()));
        }
    }

    @Test
    public void testLoadDuracloudUserByUsernameException0() throws Exception {
        String username = "junk-username";
        Exception exception = new DBNotFoundException("canned-exception");
        setUpLoadDuracloudUserByUsername(username, exception);

        try {
            userService.loadDuracloudUserByUsername(username);
            Assert.fail("exception expected");
        } catch (DBNotFoundException e) {
            Assert.assertEquals(exception, e);
        }
    }

    @Test
    public void testLoadDuracloudUserByUsernameException1() throws Exception {
        String username = "junk-username";
        Exception exception = new DBUninitializedException("canned-exception");
        setUpLoadDuracloudUserByUsername(username, exception);

        try {
            userService.loadDuracloudUserByUsername(username);
            Assert.fail("exception expected");
        } catch (DBUninitializedException e) {
            Assert.assertEquals(exception, e);
        }
    }

    private void setUpLoadDuracloudUserByUsername(String username,
                                                  Exception exception)
        throws Exception {
        DuracloudUser user = newDuracloudUser(userId, username);

        if (null == exception) {
            EasyMock.expect(userRepo.findByUsername(EasyMock.isA(String.class)))
                .andReturn(user)
                .anyTimes();

            EasyMock.expect(rightsRepo.findByUserId(EasyMock.anyInt()))
                .andReturn(getRightsSet(Role.ROLE_USER))
                .anyTimes();            
        } else {
            EasyMock.expect(userRepo.findByUsername(EasyMock.isA(String.class)))
                .andThrow(exception);
        }

        replayMocks();
    }

    private Set<AccountRights> getRightsSet(Role role) {
        Set<AccountRights> rights = new HashSet<AccountRights>();
        rights.add(
            new AccountRights(0, acctId, userId, role.getRoleHierarchy()));
        return rights;
    }

}