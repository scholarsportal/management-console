/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudUserService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.access.AccessDecisionVoter.ACCESS_ABSTAIN;
import static org.springframework.security.access.AccessDecisionVoter.ACCESS_DENIED;
import static org.springframework.security.access.AccessDecisionVoter.ACCESS_GRANTED;

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
        int accountId = 0;
        int userId = 1;

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
        int accountId = 0;
        int userId = 1;
        Object[] params = { accountId, userId };
        expectNotRightsForAccount(accountId);
        testRevokeUserRights(ACCESS_GRANTED, USER_AUTHORITIES, params);
    }

    private void expectRightsForAccount(int accountId, int userId)
        throws DBNotFoundException{
        reinitRightsMock();
        Set<AccountRights> set = new HashSet<AccountRights>();
        set.add(new AccountRights(1, accountId, userId, OWNER_AUTHORITIES));
        EasyMock.expect(adv.getDuracloudRepoMgr().getRightsRepo().findByAccountId(accountId))
        .andReturn(set);
        replayRightsMock();        
    }

    private void expectNotRightsForAccount(int accountId) {
        reinitRightsMock();
        EasyMock.expect(adv.getDuracloudRepoMgr().getRightsRepo().findByAccountId(accountId))
            .andReturn(new HashSet<AccountRights>());
        replayRightsMock();        
    }

    @Test
    public void testOwnerGrantRevoke() throws Exception{
        int accountId = 0;
        int userId = 1;

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
        int accountId = 0;
        int userId = 1;
        reinitRightsMock();

        //admin grants admin rights to user
        expect(0,1, USER_AUTHORITIES);
        Role[] roles = new Role[]{Role.ROLE_ADMIN};
        Object[] adminRoleParams = { accountId, userId, roles };
        testSetUserRights(ACCESS_GRANTED, ADMIN_AUTHORITIES, adminRoleParams);

        //admin grants owner rights to admin
        expect(0,1, ADMIN_AUTHORITIES);
        roles = new Role[]{Role.ROLE_OWNER};
        Object[] ownerRoleParams = { accountId, userId, roles };
        testSetUserRights(ACCESS_DENIED, ADMIN_AUTHORITIES, ownerRoleParams);

        //admin revokes admin rights from owner
        expect(0,1, OWNER_AUTHORITIES);
        testSetUserRights(ACCESS_DENIED, ADMIN_AUTHORITIES, adminRoleParams);

        //admin revokes owner rights from owner
        testSetUserRights(ACCESS_DENIED, ADMIN_AUTHORITIES, ownerRoleParams);
    }    

    private void expect(int accountId, int userId, Set<Role> authorities)
        throws Exception {
        reinitRightsMock();
        EasyMock.expect(adv.getDuracloudRepoMgr().getRightsRepo().
            findByAccountIdAndUserId(accountId, userId))
        .andReturn(new AccountRights(1, accountId, userId, authorities));
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
                                                         Integer.TYPE,
                                                         Integer.TYPE,
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
                                                         Integer.TYPE,
                                                         Integer.TYPE);
        int voteResult = adv.vote(createUserAuthentication(callerAuthorities),
                                  createMockMethodInvoker(method, params),
                                  attributes);
        Assert.assertEquals(access, voteResult);
    }

}
