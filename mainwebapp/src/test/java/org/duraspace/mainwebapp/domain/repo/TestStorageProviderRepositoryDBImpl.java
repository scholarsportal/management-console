
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.DatabaseUtil;
import org.duraspace.mainwebapp.domain.model.StorageProvider;
import org.duraspace.mainwebapp.domain.repo.db.MainDatabaseUtil;
import org.duraspace.storage.domain.StorageProviderType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestStorageProviderRepositoryDBImpl {

    private final DatabaseUtil dbUtil;

    private final String baseDir = "testMainDB";

    private final Credential dbCred = new Credential("duraspace", "duraspace");

    private StorageProviderRepositoryDBImpl repo;

    private final String tablename =
            StorageProviderRepositoryDBImpl.getTableSpec().getTableName();

    private StorageProvider providerA;

    private final String providerName = "providerName";

    private final StorageProviderType providerType =
            StorageProviderType.AMAZON_S3;

    private final String url = "url";

    public TestStorageProviderRepositoryDBImpl()
            throws Exception {
        dbUtil = new MainDatabaseUtil(dbCred, baseDir);
        dbUtil.initializeDB();
    }

    @Before
    public void setUp() throws Exception {
        repo = new StorageProviderRepositoryDBImpl();
        repo.setDataSource(dbUtil.getDataSource());

        providerA = new StorageProvider();
        providerA.setProviderName(providerName);
        providerA.setProviderType(providerType);
        providerA.setUrl(url);
    }

    @After
    public void tearDown() throws Exception {
        repo = null;
        providerA = null;
        dbUtil.clearDB();
    }

    @Test
    public void testSaveAddress() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        int id = repo.saveStorageProvider(providerA);

        verifyTableSize(4);

        StorageProvider provider = repo.findStorageProviderById(id);
        assertNotNull(provider);
        assertEquals(providerName, provider.getProviderName());
        assertEquals(providerType, provider.getProviderType());
        assertEquals(url, provider.getUrl());
    }

    @Test
    public void testRetrieval() throws Exception {
        verifyTableSize(0);
        insertTestData(1);
        verifyTableSize(1);

        List<Integer> ids = repo.getStorageProviderIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 1);

        StorageProvider provider = null;
        for (int id : ids) {
            provider = repo.findStorageProviderById(id);
            assertNotNull(provider);
            assertEquals(providerName + 0, provider.getProviderName());
            assertEquals(StorageProviderType.UNKNOWN, provider
                    .getProviderType());
            assertEquals(url + 0, provider.getUrl());
        }
    }

    @Test
    public void testBadRetrieval() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<Integer> ids = repo.getStorageProviderIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        StorageProvider provider = null;
        try {
            provider = repo.findStorageProviderById(-99);
            Assert.fail("Should throw exception.");
        } catch (Exception e) {
        }
        assertTrue(provider == null);
    }

    @Test
    public void testFindIdByProviderType() throws Exception {
        verifyTableSize(0);
        insertTestData(1);
        verifyTableSize(1);

        assertTrue(repo
                .findStorageProviderIdByProviderType(StorageProviderType.UNKNOWN) > 0);

        try {
            repo.findStorageProviderIdByProviderType(null);
            fail("Should throw exception");
        } catch (Exception e) {
        }

    }

    @SuppressWarnings("unchecked")
    private void verifyTableSize(int size) {
        List results =
                dbUtil.getOps().queryForList("SELECT * FROM " + tablename);
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == size);
    }

    private void insertTestData(int size) {

        StorageProviderType providerType = StorageProviderType.AMAZON_S3;

        for (int i = 0; i < size; ++i) {
            switch (i) {
                case 0:
                    providerType = StorageProviderType.UNKNOWN;
                    break;
                case 1:
                    providerType = StorageProviderType.MICROSOFT_AZURE;
                    break;
                case 2:
                    providerType = StorageProviderType.SUN;
            }
            dbUtil.getOps().update("INSERT INTO " + tablename
                    + " (providerName,providerType,url) VALUES (" + "'"
                    + providerName + i + "','" + providerType + "','" + url + i
                    + "')");
        }
    }

}
