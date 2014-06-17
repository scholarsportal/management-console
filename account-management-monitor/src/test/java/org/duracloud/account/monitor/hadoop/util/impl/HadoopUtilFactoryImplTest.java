/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.hadoop.util.impl;

import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.monitor.error.HadoopNotActivatedException;
import org.duracloud.account.monitor.error.UnsupportedStorageProviderException;
import org.duracloud.account.monitor.hadoop.util.HadoopUtil;
import org.duracloud.account.monitor.hadoop.util.HadoopUtilFactory;
import org.duracloud.account.monitor.hadoop.util.impl.HadoopUtilFactoryImpl;
import org.duracloud.storage.domain.StorageProviderType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 7/11/11
 */
public class HadoopUtilFactoryImplTest {

    private HadoopUtilFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new HadoopUtilFactoryImpl();
    }

    @Test
    public void testGetHadoopUtil() throws Exception {
        StorageProviderType type = StorageProviderType.AMAZON_S3;

        Long id0 = 0L;
        StorageProviderAccount storageAcct0 = createStorageAccount(id0, type);

        Long id1 = 1L;
        StorageProviderAccount storageAcct1 = createStorageAccount(id1, type);

        HadoopUtil hadoopUtil0 = factory.getHadoopUtil(storageAcct0);
        Assert.assertNotNull(hadoopUtil0);

        HadoopUtil hadoopUtil1 = factory.getHadoopUtil(storageAcct1);
        Assert.assertNotNull(hadoopUtil1);

        HadoopUtil hadoopUtilX = factory.getHadoopUtil(storageAcct0);
        Assert.assertNotNull(hadoopUtilX);
        Assert.assertEquals(hadoopUtil0, hadoopUtilX);
    }

    @Test
    public void testGetHadoopUtilError() throws HadoopNotActivatedException {
        Long id = 0L;
        StorageProviderType type = StorageProviderType.RACKSPACE;
        StorageProviderAccount storageAcct = createStorageAccount(id, type);

        boolean thrown = false;
        try {
            factory.getHadoopUtil(storageAcct);
            Assert.fail("exception expected");

        } catch (UnsupportedStorageProviderException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    private StorageProviderAccount createStorageAccount(Long id,
                                                        StorageProviderType type) {
        String username = "username-" + id;
        String password = "password-" + id;
        boolean rrs = false;

        return new StorageProviderAccount(id, type, username, password, rrs);
    }
}
