/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import java.util.List;
import java.util.Map;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserServiceImplTest {

    private DuracloudUserServiceImpl service;
    private DuracloudUserRepo userRepo;
    private DuracloudAccountRepo accountRepo;

    private static final String username = "username";
    private static final String password = "password";
    private static final String firstName = "firstName";
    private static final String lastName = "lastName";
    private static final String email = "email";

    private static final String acctId = "acct-id";

    @Before
	public void before() {
		service = new DuracloudUserServiceImpl(
				AccountUtilTestData.createMockUserRepo(), 
				AccountUtilTestData.createMockAccountRepo());
	}

    @Test
    public void testIsUsernameAvailable() throws Exception {
        Assert.assertFalse(service.isUsernameAvailable(AccountUtilTestData.USERNAMES[0]));
        Assert.assertTrue(service.isUsernameAvailable(AccountUtilTestData.NOT_A_USER_USERNAME));
    }



    private DuracloudUser createUser() {
        return new DuracloudUser(username,
                                 password,
                                 firstName,
                                 lastName,
                                 email);
    }

    @Test
    public void testCreateNewUser() throws Exception {
        userRepo = createMockCreateNewUserRepo();
        service = new DuracloudUserServiceImpl(userRepo, accountRepo);

        String id = service.createNewUser(username,
                                          password,
                                          firstName,
                                          lastName,
                                          email);
        Assert.assertNotNull(id);
        Assert.assertEquals(username, id);

        boolean thrown = false;
        try {
            service.createNewUser(username,
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

    private DuracloudUserRepo createMockCreateNewUserRepo() throws Exception {
        DuracloudUserRepo repo = EasyMock.createMock(DuracloudUserRepo.class);

        EasyMock.expect(repo.findById(username))
            .andThrow(new DBNotFoundException(null));

        repo.save(EasyMock.isA(DuracloudUser.class));
        EasyMock.expectLastCall();

        EasyMock.expect(repo.findById(username)).andReturn(null);

        EasyMock.replay(repo);
        return repo;
    }

    @Test
    public void testAddUserToAccount() throws Exception {
        DuracloudUser user = createUser();
        Map<String, List<String>> acctToRoles = user.getAcctToRoles();
        Assert.assertNotNull(acctToRoles);
        Assert.assertTrue(!acctToRoles.containsKey(acctId));

        userRepo = createMockAddUserToAccountRepo(user);
        service = new DuracloudUserServiceImpl(userRepo, accountRepo);

        service.addUserToAccount(acctId, username);

        acctToRoles = user.getAcctToRoles();
        Assert.assertNotNull(acctToRoles);
        Assert.assertTrue(acctToRoles.containsKey(acctId));
        Assert.assertTrue(acctToRoles.get(acctId).contains(Role.ROLE_USER.name()));

        EasyMock.verify(userRepo);
    }

    private DuracloudUserRepo createMockAddUserToAccountRepo(DuracloudUser user)
        throws DBNotFoundException, DBConcurrentUpdateException {
        DuracloudUserRepo repo = EasyMock.createMock(DuracloudUserRepo.class);

        EasyMock.expect(repo.findById(username)).andReturn(user);

        EasyMock.replay(repo);
        return repo;
    }

    @Test
    public void testRemoveUserFromAccount() throws Exception {
    	//TODO: complete test
    }

    @Test
    public void testGrantAdminRights() throws Exception {
    	//TODO: complete test
    }

    @Test
    public void testRevokeAdminRights() throws Exception {
        // TODO: complete test

    }

    @Test
    public void testSendPasswordReminder() throws Exception {
        // TODO: complete test
    }

    @Test
    public void testChangePassword() throws Exception {
        // TODO: complete test
    }
    
    @Test
    public void testloadDuracloudUserByUsername() throws Exception {
        // TODO: complete test
    }
    
    @Test
    public void testGrantOwnerRights() throws Exception {
        // TODO: complete test
    }

    @Test
    public void testRevokeOwnerRights() throws Exception {
        // TODO: complete test
    }

}
