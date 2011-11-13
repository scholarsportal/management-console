/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.db.BaseRepo;
import org.duracloud.account.db.error.DBException;
import org.duracloud.common.model.Credential;
import org.duracloud.storage.domain.StorageProviderType;
import org.duracloud.unittestdb.UnitTestDatabaseUtil;
import org.duracloud.unittestdb.domain.ResourceType;
import org.junit.Assert;

import java.util.Set;

import static junit.framework.Assert.assertNotNull;

/**
 * @author: Bill Branan
 * Date: Dec 3, 2010
 */
public class BaseTestDuracloudRepoImpl {

    protected static AmazonSimpleDBClientMgr getDBManager() throws Exception {
        Credential cred = getCredential();
        AmazonSimpleDBClientMgr mgr =
            new AmazonSimpleDBClientMgr(cred.getUsername(), cred.getPassword());
        return mgr;
    }

    protected static Credential getCredential() throws Exception {
        UnitTestDatabaseUtil dbUtil = new UnitTestDatabaseUtil();
        Credential s3Credential = dbUtil.findCredentialForResource(ResourceType.fromStorageProviderType(
            StorageProviderType.AMAZON_S3));
        assertNotNull(s3Credential);
        assertNotNull(s3Credential.getUsername());
        assertNotNull(s3Credential.getPassword());

        return s3Credential;
    }

    protected static abstract class DBCaller<T> {

        public Object call(T expected) {
            Object result = null;
            boolean callComplete = false;
            int maxTries = 20;
            int tries = 0;

            while (!callComplete && tries < maxTries) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    // do nothing
                }
                try {
                    result = doCall();
                    callComplete = expected.equals(result);

                } catch (DBException dbe) {
                    callComplete = true;

                } catch (Exception e) {
                    // do nothing
                }
                tries++;
            }
            Assert.assertTrue(
                expected + " not found after " + tries + " tries.",
                callComplete);

            return result;
        }

        protected abstract T doCall() throws Exception;
    }

    protected static abstract class DBCallerVarArg<T> {

        public void call(T... allExpected) {
            boolean callComplete = false;
            int maxTries = 20;
            int tries = 0;

            Set<T> allReceived = null;
            while (!callComplete && tries < maxTries) {
                try {
                    allReceived = doCall();
                    int expectedCount = allExpected.length;
                    if(allReceived.size() == expectedCount) {
                        int equalsCount = 0;
                        for(T received : allReceived) {
                            for(T expected : allExpected) {
                                if(received.equals(expected)) {
                                    equalsCount++;
                                }
                            }
                        }
                        if(equalsCount == expectedCount) {
                            callComplete = true;
                        }
                    }
                } catch (DBException dbe) {
                    callComplete = true;

                } catch (Exception e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        // do nothing
                    }
                }
                tries++;
            }
            Assert.assertTrue(
                "expected: " + allReceived + ", expected set not found after " +
                    tries + " tries.", callComplete);
        }

        protected abstract Set<T> doCall() throws Exception;
    }

    protected void verifyRepoSize(final BaseRepo repo,
                                  final int expectedSize) {
        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return repo.getIds().size();
            }
        }.call(expectedSize);
    }   
    
}
