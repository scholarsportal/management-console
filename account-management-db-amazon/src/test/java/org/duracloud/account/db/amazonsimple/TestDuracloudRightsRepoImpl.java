/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: Dec 3, 2010
 */
public class TestDuracloudRightsRepoImpl extends BaseTestDuracloudRepoImpl {

    private DuracloudRightsRepoImpl rightsRepo;

    private static final String DOMAIN = "TEST_DURACLOUD_RIGHTS";

    @Before
    public void setUp() throws Exception {
        rightsRepo = createRightsRepo();
    }

    private static DuracloudRightsRepoImpl createRightsRepo() throws Exception {
        return new DuracloudRightsRepoImpl(getDBManager(), DOMAIN);
    }

    @After
    public void tearDown() throws Exception {
        for (Integer itemId : rightsRepo.getItemIds()) {
            rightsRepo.delete(itemId);
        }
        verifyRepoSize(rightsRepo, 0);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        createRightsRepo().removeDomain();
    }

    @Test
    public void testNotFound() {
        boolean thrown = false;
        try {
            rightsRepo.findById(-100);
            Assert.fail("exception expected");

        } catch (DBNotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetIds() throws Exception {
        AccountRights rights0 = createRights(1, 1, 1);
        AccountRights rights1 = createRights(2, 1, 2);
        AccountRights rights2 = createRights(3, 2, 2);

        rightsRepo.save(rights0);
        rightsRepo.save(rights1);
        rightsRepo.save(rights2);

        List<Integer> expectedIds = new ArrayList<Integer>();
        expectedIds.add(rights0.getId());
        expectedIds.add(rights1.getId());
        expectedIds.add(rights2.getId());

        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return rightsRepo.getIds().size();
            }
        }.call(expectedIds.size());

        verifyRights(rights0);
        verifyRights(rights1);
        verifyRights(rights2);

        verifyRightsByAccountId(1, rights0, rights1);
        verifyRightsByAccountId(2, rights2);

        verifyRightsByUserId(1, rights0);
        verifyRightsByUserId(2, rights1, rights2);

        verifyRightsByAccountIdAndUserId(1, 1, rights0);
        verifyRightsByAccountIdAndUserId(1, 2, rights1);
        verifyRightsByAccountIdAndUserId(2, 2, rights2);

        // test concurrency
        verifyCounter(rights0, 1);

        AccountRights rights = null;
        while (null == rights) {
            rights = rightsRepo.findById(rights0.getId());
        }
        Assert.assertNotNull(rights);

        boolean thrown = false;
        try {
            rightsRepo.save(rights);
            rightsRepo.save(rights);
            rightsRepo.save(rights);
            rightsRepo.save(rights);

        } catch (DBConcurrentUpdateException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);

        verifyCounter(rights0, 2);
    }

    @Test
    public void testDelete() throws Exception {
        AccountRights rights0 = createRights(1, 1, 1);
        rightsRepo.save(rights0);
        verifyRepoSize(rightsRepo, 1);

        rightsRepo.delete(rights0.getId());
        verifyRepoSize(rightsRepo, 0);
    }

    @Test
    public void testRootFindByUserId()
        throws DBConcurrentUpdateException, DBNotFoundException {
        int userId1 = 1;
        int userId2 = 2;
        int userIdR = 3; // root

        AccountRights rights0 = createRights(1, 1, userId1);
        AccountRights rights1 = createRights(2, 1, userId2);
        AccountRights rights2 = createRights(3, 2, userId2);
        AccountRights rightsR = createRights(4, 0, userIdR, Role.ROLE_ROOT);

        rightsRepo.save(rights0);
        rightsRepo.save(rights1);
        rightsRepo.save(rights2);
        rightsRepo.save(rightsR);

        // make sure all saves have committed
        verifyRepoSize(rightsRepo, 4);

        verifyRightsByUserId(userId1, rights0);
        verifyRightsByUserId(userId2, rights1, rights2);
        verifyRightsByUserId(userIdR,
                             createRights(5, 1, userIdR, Role.ROLE_ROOT),
                             createRights(6, 2, userIdR, Role.ROLE_ROOT),
                             createRights(7, 0, userIdR, Role.ROLE_ROOT));

    }

    @Test
    public void testRootFindByAcctId() throws DBConcurrentUpdateException {
        int userId1 = 1;
        int userId2 = 2;
        int userIdR = 3; // root

        AccountRights rights0 = createRights(1, 1, userId1);
        AccountRights rights1 = createRights(2, 1, userId2);
        AccountRights rights2 = createRights(3, 2, userId2);
        AccountRights rightsR = createRights(4, 0, userIdR, Role.ROLE_ROOT);

        rightsRepo.save(rights0);
        rightsRepo.save(rights1);
        rightsRepo.save(rights2);
        rightsRepo.save(rightsR);

        // make sure all saves have committed
        verifyRepoSize(rightsRepo, 4);

        AccountRights rootRights1 = createRights(4, 1, userIdR, Role.ROLE_ROOT);
        verifyRightsByAccountId(1, rights0, rights1, rootRights1);

        AccountRights rootRights2 = createRights(4, 2, userIdR, Role.ROLE_ROOT);
        verifyRightsByAccountId(2, rights2, rootRights2);
    }


    @Test
    public void testRootFindByAcctIdAndUserId()
        throws DBConcurrentUpdateException, DBNotFoundException {
        int userId1 = 1;
        int userId2 = 2;
        int userIdR = 3; // root

        AccountRights rights0 = createRights(1, 1, userId1);
        AccountRights rights1 = createRights(2, 1, userId2);
        AccountRights rights2 = createRights(3, 2, userId2);
        AccountRights rightsR = createRights(4, 0, userIdR, Role.ROLE_ROOT);

        rightsRepo.save(rights0);
        rightsRepo.save(rights1);
        rightsRepo.save(rights2);
        rightsRepo.save(rightsR);

        // make sure all saves have committed
        verifyRepoSize(rightsRepo, 4);

        // check standard cases
        verifyRightsByAccountIdAndUserId(1, userId1, rights0);
        verifyRightsByAccountIdAndUserId(1, userId2, rights1);
        verifyRightsByAccountIdAndUserId(2, userId2, rights2);

        // check root cases
        AccountRights rootRights1 = createRights(4, 1, userIdR, Role.ROLE_ROOT);
        verifyRightsByAccountIdAndUserId(1, userIdR, rootRights1);

        AccountRights rootRights2 = createRights(4, 2, userIdR, Role.ROLE_ROOT);
        verifyRightsByAccountIdAndUserId(2, userIdR, rootRights2);

        // search for non-existent user
        verifyInvalidCases(1, 99);

        // search for non-existent acct (with admin)
        verifyInvalidCases(99, userId1);

        // search for non-existent acct (with root)
        verifyInvalidCases(99, userIdR);
    }

    private void verifyInvalidCases(int acctId, int userId) {
        boolean thrown = false;
        try {
            rightsRepo.findByAccountIdAndUserId(acctId, userId);
            Assert.fail("exception expected");

        } catch (Exception e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }


    private AccountRights createRights(int id,
                                       int accountId,
                                       int userId,
                                       Role role) {
        Set<Role> roles = role.getRoleHierarchy();
        return new AccountRights(id, accountId, userId, roles);
    }

    private AccountRights createRights(int id, int accountId, int userId) {
        return createRights(id, accountId, userId, Role.ROLE_ADMIN);
    }

    private void verifyRights(final AccountRights rights) {
        new DBCaller<AccountRights>() {
            protected AccountRights doCall() throws Exception {
                return rightsRepo.findById(rights.getId());
            }
        }.call(rights);
    }

    private void verifyRightsByAccountId(final int accountId,
                                         final AccountRights... rights) {
        new DBCallerVarArg<AccountRights>() {
            protected Set<AccountRights> doCall() throws Exception {
                return rightsRepo.findByAccountId(accountId);
            }
        }.call(rights);
    }

    private void verifyRightsByUserId(final int userId,
                                      final AccountRights... rights) {
        new DBCallerVarArg<AccountRights>() {
            protected Set<AccountRights> doCall() throws Exception {
                return rightsRepo.findByUserId(userId);
            }
        }.call(rights);
    }

    private void verifyRightsByAccountIdAndUserId(final int accountId,
                                                  final int userId,
                                                  final AccountRights rights) {
        new DBCaller<AccountRights>() {
            protected AccountRights doCall() throws Exception {
                return rightsRepo.findByAccountIdAndUserId(accountId, userId);
            }
        }.call(rights);
    }

    private void verifyCounter(final AccountRights rights, final int counter) {
        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return rightsRepo.findById(rights.getId()).getCounter();
            }
        }.call(counter);
    }

}
