/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
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

    private static final int accountId = 100;
    private static final int userId = 200;
    private static Set<Role> roles;

    @BeforeClass
    public static void initialize() throws Exception {
        roles = new HashSet<Role>();
        roles.add(Role.ROLE_USER);
        roles.add(Role.ROLE_ADMIN);
    }

    @Before
    public void setUp() throws Exception {
        rightsRepo = createRightsRepo();
    }

    private static DuracloudRightsRepoImpl createRightsRepo() throws Exception {
        return new DuracloudRightsRepoImpl(getDBManager(), DOMAIN);
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
        AccountRights rights0 = createRights(0, 0, 0);
        AccountRights rights1 = createRights(1, 0, 1);
        AccountRights rights2 = createRights(2, 1, 1);

        rightsRepo.save(rights0);
        rightsRepo.save(rights1);
        rightsRepo.save(rights2);

        List<String> expectedIds = new ArrayList<String>();
        expectedIds.add(String.valueOf(rights0.getId()));
        expectedIds.add(String.valueOf(rights1.getId()));
        expectedIds.add(String.valueOf(rights2.getId()));

        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return rightsRepo.getIds().size();
            }
        }.call(expectedIds.size());

        verifyRights(rights0);
        verifyRights(rights1);
        verifyRights(rights2);

        verifyRightsByAccountId(0, rights0, rights1);
        verifyRightsByAccountId(1, rights2);

        verifyRightsByUserId(0, rights0);
        verifyRightsByUserId(1, rights1, rights2);

        verifyRightsByAccountIdAndUserId(0, 0, rights0);
        verifyRightsByAccountIdAndUserId(0, 1, rights1);
        verifyRightsByAccountIdAndUserId(1, 1, rights2);

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

    private AccountRights createRights(int id, int accountId, int userId) {
        return new AccountRights(id, accountId, userId, roles);
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
