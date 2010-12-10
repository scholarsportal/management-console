/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBUninitializedException;
import org.duracloud.account.db.impl.IdUtilImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: Dec 9, 2010
 */
public class AmazonSimpleDBRepoMgrTest {

    private AmazonSimpleDBRepoMgr repoMgr;
    private IdUtil idUtil;

    @Before
    public void setUp() throws Exception {
        idUtil = new IdUtilImpl();
        repoMgr = new AmazonSimpleDBRepoMgr(idUtil);
    }

    @Test
    public void testNotInitialize() throws Exception {
        int numExceptions = 0;
        try {
            repoMgr.getUserRepo();
            Assert.fail("exception expected");
        } catch (DBUninitializedException e) {
            numExceptions++;
        }

        try {
            repoMgr.getAccountRepo();
            Assert.fail("exception expected");
        } catch (DBUninitializedException e) {
            numExceptions++;
        }

        try {
            repoMgr.getRightsRepo();
            Assert.fail("exception expected");
        } catch (DBUninitializedException e) {
            numExceptions++;
        }

        try {
            repoMgr.getInstanceRepo();
            Assert.fail("exception expected");
        } catch (DBUninitializedException e) {
            numExceptions++;
        }

        try {
            repoMgr.getImageRepo();
            Assert.fail("exception expected");
        } catch (DBUninitializedException e) {
            numExceptions++;
        }

        // IdUtil only throws when a direct call is made.
        try {
            repoMgr.getIdUtil().newAccountId();
            Assert.fail("exception expected");
        } catch (DBUninitializedException e) {
            numExceptions++;
        }

        Assert.assertEquals(6, numExceptions);
    }

}
