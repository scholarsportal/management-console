package org.duracloud.mainwebapp.domain.repo;

import org.duracloud.common.model.Credential;
import org.duracloud.common.util.DatabaseUtil;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.mainwebapp.domain.model.ComputeProvider;
import org.duracloud.mainwebapp.domain.repo.db.MainDatabaseUtil;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestComputeProviderRepositoryDBImpl {

    private final DatabaseUtil dbUtil;

    private final String baseDir = "testMainDB";

    private final Credential dbCred = new Credential("duracloud", "duracloud");

    private ComputeProviderRepositoryDBImpl repo;

    private final String tablename =
            ComputeProviderRepositoryDBImpl.getTableSpec().getTableName();

    private ComputeProvider providerA;

    private final String providerName = "providerName";

    private final ComputeProviderType providerType =
            ComputeProviderType.AMAZON_EC2;

    private final String url = "url";

    public TestComputeProviderRepositoryDBImpl()
            throws Exception {
        dbUtil = new MainDatabaseUtil(dbCred, baseDir);
        dbUtil.initializeDB();
    }

    @Before
    public void setUp() throws Exception {
        repo = new ComputeProviderRepositoryDBImpl();
        repo.setDataSource(dbUtil.getDataSource());

        providerA = new ComputeProvider();
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

        int id = repo.saveComputeProvider(providerA);

        verifyTableSize(4);

        ComputeProvider provider = repo.findComputeProviderById(id);
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

        List<Integer> ids = repo.getComputeProviderIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 1);

        ComputeProvider provider = null;
        for (int id : ids) {
            provider = repo.findComputeProviderById(id);
            assertNotNull(provider);
            assertEquals(providerName + 0, provider.getProviderName());
            assertEquals(ComputeProviderType.UNKNOWN, provider
                    .getProviderType());
            assertEquals(url + 0, provider.getUrl());
        }
    }

    @Test
    public void testBadRetrieval() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<Integer> ids = repo.getComputeProviderIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        ComputeProvider provider = null;
        try {
            provider = repo.findComputeProviderById(-99);
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
                .findComputeProviderIdByProviderType(ComputeProviderType.UNKNOWN) > 0);

        try {
            repo.findComputeProviderIdByProviderType(null);
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

        for (int i = 0; i < size; ++i) {

            ComputeProviderType providerTypeX = null;
            switch (i) {
                case 0:
                    providerTypeX = ComputeProviderType.UNKNOWN;
                    break;
                case 1:
                    providerTypeX = ComputeProviderType.MICROSOFT_AZURE;
                    break;
                case 2:
                    providerTypeX = ComputeProviderType.RACKSPACE_CLOUDSERVERS;
                    break;
                default:
                    providerTypeX = ComputeProviderType.AMAZON_EC2;

            }
            dbUtil.getOps().update("INSERT INTO " + tablename
                    + " (providerName,providerType,url) VALUES (" + "'"
                    + providerName + i + "','" + providerTypeX + "','" + url
                    + i + "')");
        }
    }

}
