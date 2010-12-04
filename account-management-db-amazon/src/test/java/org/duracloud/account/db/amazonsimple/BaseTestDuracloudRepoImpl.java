/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

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

        public void call(T expected) {
            boolean callComplete = false;
            int maxTries = 20;
            int tries = 0;

            while (!callComplete && tries < maxTries) {
                try {
                    callComplete = expected.equals(doCall());

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
                expected + " not found after " + tries + " tries.",
                callComplete);
        }

        protected abstract T doCall() throws Exception;
    }

    protected static abstract class DBCallerVarArg<T> {

        public void call(T... allExpected) {
            boolean callComplete = false;
            int maxTries = 20;
            int tries = 0;

            while (!callComplete && tries < maxTries) {
                try {
                    Set<T> allReceived = doCall();
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
                "expected set not found after " + tries + " tries.",
                callComplete);
        }

        protected abstract Set<T> doCall() throws Exception;
    }
    
}
