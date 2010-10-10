/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserServiceImplTest {

    private DuracloudUserServiceImpl service;
    private DuracloudUserRepo duracloudUserRepo;

    private static final String username = "username";
    private static final String password = "password";
    private static final String firstName = "firstName";
    private static final String lastName = "lastName";
    private static final String email = "email";

    @Test
    public void testIsUsernameAvailable() throws Exception {
        // TODO: complete test
    }

    @Test
    public void testCreateNewUser() throws Exception {
        setUpCreateNewUser();
        service.createNewUser(username, password, firstName, lastName, email);
        tearDownCreateNewUser();
    }

    private void setUpCreateNewUser() throws Exception {
        duracloudUserRepo = createMockDuracloudUserRepo();
        service = new DuracloudUserServiceImpl(duracloudUserRepo);
    }

    private DuracloudUserRepo createMockDuracloudUserRepo() throws Exception {
        DuracloudUserRepo repo = EasyMock.createMock(DuracloudUserRepo.class);
        repo.save(EasyMock.isA(DuracloudUser.class));
        EasyMock.expectLastCall();

        EasyMock.expect(repo.findById(username))
            .andThrow(new DBNotFoundException(null));

        EasyMock.replay(repo);
        return repo;
    }

    private void tearDownCreateNewUser() {
        EasyMock.verify(duracloudUserRepo);
    }

    @Test
    public void testAddUserToAccount() throws Exception {
        // TODO: complete test
    }

    @Test
    public void testRemoveUserFromAccount() throws Exception {
        // TODO: complete test
    }

    @Test
    public void testGrantAdminRights() throws Exception {
        // TODO: complete test
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
}
