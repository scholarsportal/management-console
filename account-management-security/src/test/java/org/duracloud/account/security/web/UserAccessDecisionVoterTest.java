/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import static org.springframework.security.access.AccessDecisionVoter.ACCESS_ABSTAIN;
import static org.springframework.security.access.AccessDecisionVoter.ACCESS_DENIED;
import static org.springframework.security.access.AccessDecisionVoter.ACCESS_GRANTED;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.Role;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.repo.DuracloudRightsRepo;
import org.duracloud.account.db.util.DuracloudUserService;
import org.duracloud.account.db.util.error.DBNotFoundException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class UserAccessDecisionVoterTest extends AccessDecisionVoterTestBase {

    private static final String LOAD_DURACLOUD_USER_BY_USERNAME =
        "loadDuracloudUserByUsername";

    private UserAccessDecisionVoter adv;

    private DuracloudUserService userService;

    @Before
    public void before() {
        userService = EasyMock.createMock(DuracloudUserService.class);
        EasyMock.replay(userService);

        adv = new UserAccessDecisionVoter();
    }

    @Test
    public void testAbstain() throws Exception {
        Assert.assertEquals(ACCESS_ABSTAIN,
            adv.vote(createUserAuthentication(USER_AUTHORITIES),
                createMockMethodInvoker(userService.getClass()
                    .getMethod("toString"), new Object[] { TEST_USERNAME }),
                attributes));
    }

    @Test
    public void testLoadUserByUsernameDeny() throws Exception {
        Assert.assertEquals(ACCESS_DENIED,
            adv.vote(createUserAuthentication(USER_AUTHORITIES),
                createMockMethodInvoker(userService.getClass()
                    .getMethod(LOAD_DURACLOUD_USER_BY_USERNAME, String.class),
                    new Object[] { "other" }),
                attributes));
    }

    @Test
    public void testLoadUserByUsernamRootAccess() throws Exception {
        Assert.assertEquals(ACCESS_GRANTED,
            adv.vote(createRootAuthentication(),
                createMockMethodInvoker(userService.getClass()
                    .getMethod(LOAD_DURACLOUD_USER_BY_USERNAME, String.class),
                    new Object[] { "other" }),
                attributes));
    }

    @Test
    public void testLoadUserByUsernamGranted() throws Exception {
        Assert.assertEquals(ACCESS_GRANTED,
            adv.vote(createUserAuthentication(USER_AUTHORITIES),
                createMockMethodInvoker(userService.getClass()
                    .getMethod(LOAD_DURACLOUD_USER_BY_USERNAME, String.class),
                    new Object[] { TEST_USERNAME }),
                attributes));
    }

    private void testRevokeUserRights(int expectedDecision,
                                      Set<Role> callerAuthorities,
                                      Object[] params) throws Exception{
        testRevokeMethod(expectedDecision, callerAuthorities, params);
    }

    private void testSetUserRights(int expectedDecision,
                                   Set<Role> callerAuthorities,
                                   Object[] params) throws Exception{
        testSetMethod(expectedDecision, callerAuthorities, params);
    }

    @Test
    public void testUserGrantRevokeWithAccountOwnedByAnotherUser() throws Exception{
        //test user grants/revokes rights to/from anybody
        Long accountId = 0L;
        Long userId = 1L;

        // Set user rights
        expectRightsForAccount(accountId, userId);
        Role[] roles = new Role[]{Role.ROLE_USER};
        Object[] userRoleParams = { accountId, userId, roles };
        testSetUserRights(ACCESS_DENIED, USER_AUTHORITIES, userRoleParams);

        // Set admin rights
        expectRightsForAccount(accountId, userId);
        roles = new Role[]{Role.ROLE_ADMIN};
        Object[] adminRoleParams = { accountId, userId, roles };
        testSetUserRights(ACCESS_DENIED, USER_AUTHORITIES, adminRoleParams);

        // Set owner rights
        expectRightsForAccount(accountId, userId);
        roles = new Role[]{Role.ROLE_OWNER};
        Object[] ownerRoleParams = { accountId, userId, roles };
        testSetUserRights(ACCESS_DENIED, USER_AUTHORITIES, ownerRoleParams);
    }
    
    @Test 
    public void testUserGrantRevokeOnNewAccount() throws Exception{
        //ie new in the sense that no one else has rights to associated with it
        Long accountId = 0L;
        Long userId = 1L;
        Object[] params = { accountId, userId };
        expectNotRightsForAccount(accountId);
        testRevokeUserRights(ACCESS_GRANTED, USER_AUTHORITIES, params);
    }

    private void expectRightsForAccount(Long accountId, Long userId)
        throws DBNotFoundException {
        reinitRightsMock();
        List<AccountRights> set = new LinkedList<AccountRights>();

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(accountId);
        DuracloudUser user = new DuracloudUser();
        user.setId(userId);
        AccountRights accountRights = new AccountRights();
        accountRights.setId(1L);
        accountRights.setAccount(accountInfo);
        accountRights.setUser(user);
        accountRights.setRoles(OWNER_AUTHORITIES);
        set.add(accountRights);

        EasyMock.expect(adv.getDuracloudRepoMgr().getRightsRepo().findByAccountId(accountId))
                .andReturn(set);
        replayRightsMock();        
    }

    private void expectNotRightsForAccount(Long accountId) {
        reinitRightsMock();
        EasyMock.expect(adv.getDuracloudRepoMgr().getRightsRepo().findByAccountId(accountId))
            .andReturn(new LinkedList<AccountRights>());
        replayRightsMock();        
    }

    @Test
    public void testOwnerGrantRevoke() throws Exception{
        Long accountId = 0L;
        Long userId = 1L;

        // Set user rights
        Role[] roles = new Role[]{Role.ROLE_USER};
        Object[] userRoleParams = { accountId, userId, roles };
        testSetUserRights(ACCESS_GRANTED, OWNER_AUTHORITIES, userRoleParams);

        // Set admin rights
        roles = new Role[]{Role.ROLE_ADMIN};
        Object[] adminRoleParams = { accountId, userId, roles };
        testSetUserRights(ACCESS_GRANTED, OWNER_AUTHORITIES, adminRoleParams);

        // Set owner rights
        roles = new Role[]{Role.ROLE_OWNER};
        Object[] ownerRoleParams = { accountId, userId, roles };
        testSetUserRights(ACCESS_GRANTED, OWNER_AUTHORITIES, ownerRoleParams);
    }
    
    @Test
    public void testAdminGrantRevoke() throws Exception{
        Long accountId = 0L;
        Long userId = 1L;
        reinitRightsMock();

        //admin grants admin rights to user
        expect(0L,1L, USER_AUTHORITIES);
        Role[] roles = new Role[]{Role.ROLE_ADMIN};
        Object[] adminRoleParams = { accountId, userId, roles };
        testSetUserRights(ACCESS_GRANTED, ADMIN_AUTHORITIES, adminRoleParams);

        //admin grants owner rights to admin
        expect(0L,1L, ADMIN_AUTHORITIES);
        roles = new Role[]{Role.ROLE_OWNER};
        Object[] ownerRoleParams = { accountId, userId, roles };
        testSetUserRights(ACCESS_DENIED, ADMIN_AUTHORITIES, ownerRoleParams);

        //admin revokes admin rights from owner
        expect(0L,1L, OWNER_AUTHORITIES);
        testSetUserRights(ACCESS_DENIED, ADMIN_AUTHORITIES, adminRoleParams);

        //admin revokes owner rights from owner
        testSetUserRights(ACCESS_DENIED, ADMIN_AUTHORITIES, ownerRoleParams);
    }    

    private void expect(Long accountId, Long userId, Set<Role> authorities)
        throws Exception {
        reinitRightsMock();

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(accountId);
        DuracloudUser user = new DuracloudUser();
        user.setId(userId);
        AccountRights accountRights = new AccountRights();
        accountRights.setId(1L);
        accountRights.setAccount(accountInfo);
        accountRights.setUser(user);
        accountRights.setRoles(authorities);

        EasyMock.expect(adv.getDuracloudRepoMgr().getRightsRepo().
            findByAccountIdAndUserId(accountId, userId))
        .andReturn(accountRights);
        replayRightsMock();
    }

    private void replayRightsMock() {
        EasyMock.replay(this.adv.getDuracloudRepoMgr().getRightsRepo());
    }

    private void reinitRightsMock() {
        DuracloudRightsRepo rightsRepo =
            EasyMock.createMock(DuracloudRightsRepo.class);
        
        DuracloudRepoMgr repoMgr = EasyMock.createNiceMock(DuracloudRepoMgr.class);
        EasyMock.expect(repoMgr.getRightsRepo()).andReturn(rightsRepo).anyTimes();
        adv.setDuracloudRepoMgr(repoMgr);
        EasyMock.replay(repoMgr);
    }

    private void testSetMethod(int access,
                               Set<Role> callerAuthorities,
                               Object[] params) throws Exception {
        Method method = userService.getClass().getMethod("setUserRights",
                                                         Long.class,
                                                         Long.class,
                                                         Role[].class);
        int voteResult = adv.vote(createUserAuthentication(callerAuthorities),
                                  createMockMethodInvoker(method, params),
                                  attributes);
        Assert.assertEquals(access, voteResult);
    }

    private void testRevokeMethod(int access,
                                  Set<Role> callerAuthorities,
                                  Object[] params) throws Exception {
        Method method = userService.getClass().getMethod("revokeUserRights",
                                                         Long.class,
                                                         Long.class);
        int voteResult = adv.vote(createUserAuthentication(callerAuthorities),
                                  createMockMethodInvoker(method, params),
                                  attributes);
        Assert.assertEquals(access, voteResult);
    }

}
