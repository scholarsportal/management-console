/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import static org.springframework.security.access.AccessDecisionVoter.ACCESS_ABSTAIN;
import static org.springframework.security.access.AccessDecisionVoter.ACCESS_DENIED;
import static org.springframework.security.access.AccessDecisionVoter.ACCESS_GRANTED;

import java.util.HashSet;
import java.util.Set;

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

    private void testRevokeUserRights(int expectedDecision, Set<Role> callerAuthorities, Object[] params) throws Exception{
        testMethod(expectedDecision, callerAuthorities, "revokeUserRights", params);
    }

    private void testGrantUserRights(int expectedDecision, Set<Role> callerAuthorities, Object[] params) throws Exception{
        testMethod(expectedDecision, callerAuthorities, "grantUserRights", params);
    }

    private void testRevokeAdminRights(int expectedDecision, Set<Role> callerAuthorities, Object[] params) throws Exception{
        testMethod(expectedDecision, callerAuthorities, "revokeAdminRights", params);
    }

    private void testGrantAdminRights(int expectedDecision, Set<Role> callerAuthorities, Object[] params) throws Exception{
        testMethod(expectedDecision, callerAuthorities, "grantAdminRights", params);
    }

    private void testRevokeOwnerRights(int expectedDecision, Set<Role> callerAuthorities, Object[] params) throws Exception{
        testMethod(expectedDecision, callerAuthorities, "revokeOwnerRights", params);
    }

    private void testGrantOwnerRights(int expectedDecision, Set<Role> callerAuthorities, Object[] params) throws Exception{
        testMethod(expectedDecision, callerAuthorities, "grantOwnerRights", params);
    }
    
    @Test
    public void testUserGrantRevokeWithAccountOwnedByAnotherUser() throws Exception{
        // accountid, userid
        //test user grants/revokes rights to/from anybody
        int accountId = 0;
        int userId = 1;
        Object[] params = { accountId, userId };
        expectRightsForAccount(accountId, userId);
        testGrantUserRights(ACCESS_DENIED, USER_AUTHORITIES, params);
        expectRightsForAccount(accountId, userId);
        testGrantAdminRights(ACCESS_DENIED, USER_AUTHORITIES, params);
        expectRightsForAccount(accountId, userId);
        testGrantOwnerRights(ACCESS_DENIED, USER_AUTHORITIES, params);
        expectRightsForAccount(accountId, userId);
        testRevokeUserRights(ACCESS_DENIED, USER_AUTHORITIES, params);
        expectRightsForAccount(accountId, userId);
        testRevokeAdminRights(ACCESS_DENIED, USER_AUTHORITIES, params);
        expectRightsForAccount(accountId, userId);
        testRevokeOwnerRights(ACCESS_DENIED, USER_AUTHORITIES, params);
    }
    
    @Test 
    public void testUserGrantRevokeOnNewAccount() throws Exception{
        //ie new in the sense that no one else has rights to associated with it
        int accountId = 0;
        int userId = 1;
        Object[] params = { accountId, userId };
        expectNotRightsForAccount(accountId);
        testRevokeOwnerRights(ACCESS_GRANTED, USER_AUTHORITIES, params);
    }

    private void expectRightsForAccount(int accountId, int userId) throws DBNotFoundException{
        reinitRightsMock();
        Set<AccountRights> set = new HashSet<AccountRights>();
        set.add(new AccountRights(1, accountId, userId, OWNER_AUTHORITIES));
        EasyMock.expect(this.adv.getDuracloudRepoMgr().getRightsRepo().findByAccountId(accountId))
        .andReturn(set);
        replayRightsMock();        
    }

    private void expectNotRightsForAccount(int accountId) throws DBNotFoundException{
        reinitRightsMock();
        EasyMock.expect(this.adv.getDuracloudRepoMgr().getRightsRepo().findByAccountId(accountId))
        .andThrow(new DBNotFoundException("no rights for this account"));
        replayRightsMock();        
    }

    @Test
    public void testOwnerGrantRevoke() throws Exception{
        // accountid, userid
        //test user grants/revokes rights to/from anybody
        Object[] params = { 0, 1 };
        testGrantUserRights(ACCESS_GRANTED, OWNER_AUTHORITIES, params);
        testGrantAdminRights(ACCESS_GRANTED, OWNER_AUTHORITIES, params);
        testGrantOwnerRights(ACCESS_GRANTED, OWNER_AUTHORITIES, params);
        testRevokeUserRights(ACCESS_GRANTED, OWNER_AUTHORITIES, params);
        testRevokeAdminRights(ACCESS_GRANTED, OWNER_AUTHORITIES, params);
        testRevokeOwnerRights(ACCESS_GRANTED, OWNER_AUTHORITIES, params);
    }
    
    @Test
    public void testAdminGrantRevoke() throws Exception{
        // accountid, userid
        Object[] params = { 0, 1 };
        reinitRightsMock();

        //admin grants admin rights to user
        expect(0,1, USER_AUTHORITIES);
        testGrantAdminRights(ACCESS_GRANTED, ADMIN_AUTHORITIES, params);

        //admin grants owner rights to admin
        expect(0,1, ADMIN_AUTHORITIES);
        testGrantOwnerRights(ACCESS_DENIED, ADMIN_AUTHORITIES, params);

        //admin revokes admin rights from owner
        expect(0,1, OWNER_AUTHORITIES);
        testRevokeAdminRights(ACCESS_DENIED, ADMIN_AUTHORITIES, params);

        //admin revokes owner rights from owner
        testRevokeOwnerRights(ACCESS_DENIED, ADMIN_AUTHORITIES, params);

    }
    
    

    private void expect(int accountId, int userId, Set<Role> authorities) throws Exception {
        reinitRightsMock();
        EasyMock.expect(this.adv.getDuracloudRepoMgr().getRightsRepo().findByAccountIdAndUserId(accountId, userId))
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



    private void testMethod(
        int access, Set<Role> callerAuthorities, String methodName,
        Object[] params) throws Exception {
        Assert.assertEquals(access,
            adv.vote(createUserAuthentication(callerAuthorities),
                createMockMethodInvoker(userService.getClass()
                    .getMethod(methodName, Integer.TYPE, Integer.TYPE),
                    params),
                attributes));

    }

}
