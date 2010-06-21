/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.common.model.Credential;
import org.duracloud.common.util.DatabaseUtil;
import org.duracloud.mainwebapp.domain.model.StorageAcct;
import org.duracloud.mainwebapp.domain.repo.db.MainDatabaseUtil;
import org.duracloud.storage.domain.StorageProviderType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestStorageAcctRepositoryDBImpl {

    private final DatabaseUtil dbUtil;

    private final String baseDir = "testMainDB";

    private final Credential dbCred = new Credential("duracloud", "duracloud");

    private StorageAcctRepositoryDBImpl repo;

    private final String tablename =
            StorageAcctRepositoryDBImpl.getTableSpec().getTableName();

    private StorageAcct acctA;

    private final int isPrimary = 0;

    private final String namespace = "namespace";

    private final StorageProviderType providerType =
            StorageProviderType.MICROSOFT_AZURE;

    public TestStorageAcctRepositoryDBImpl()
            throws Exception {
        dbUtil = new MainDatabaseUtil(dbCred, baseDir);
        dbUtil.initializeDB();
    }

    @Before
    public void setUp() throws Exception {
        repo = new StorageAcctRepositoryDBImpl();
        repo.setDataSource(dbUtil.getDataSource());

        acctA = new StorageAcct();
        acctA.setPrimary(isPrimary);
        acctA.setNamespace(namespace);
        acctA.setStorageProviderType(providerType);

    }

    @After
    public void tearDown() throws Exception {
        repo = null;
        acctA = null;
        dbUtil.clearDB();
    }

    @Test
    public void testSaveStorageAcct() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        int id = repo.saveStorageAcct(acctA);

        verifyTableSize(4);

        StorageAcct acct = repo.findStorageAcctById(id);
        assertNotNull(acct);
        assertEquals(isPrimary, acct.getIsPrimary());
        assertEquals(namespace, acct.getNamespace());
        assertEquals(providerType, acct.getStorageProviderType());

    }

    @Test
    public void testFindStorageAcctByDuraAcctId() throws Exception {
        String duraAcctName = "testDuraAcctName";
        createTestDuraCloudAcct(duraAcctName);
        int duraAcctId = getTestDuraCloudAcctId(duraAcctName);
        assertTrue(duraAcctId > 0);

        verifyTableSize(0);

        acctA.setDuraAcctId(duraAcctId);
        insertTestData(2);
        repo.saveStorageAcct(acctA);
        verifyTableSize(3);

        List<StorageAcct> accts = repo.findStorageAcctsByDuraAcctId(duraAcctId);
        assertNotNull(accts);
        assertTrue(accts.size() == 1);

        StorageAcct acct = accts.get(0);
        assertEquals(acct.getNamespace(), namespace);
        assertEquals(acct.getDuraAcctId(), duraAcctId);

    }

    @Test
    public void testBadRetrieval() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<Integer> ids = repo.getStorageAcctIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        StorageAcct acct = null;
        try {
            acct = repo.findStorageAcctById(-99);
            Assert.fail("Should throw exception.");
        } catch (Exception e) {
        }
        assertTrue(acct == null);
    }

    @Test
    public void testUpdates() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<Integer> ids = repo.getStorageAcctIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        for (Integer id : ids) {
            // Get StorageAcct
            StorageAcct acct = repo.findStorageAcctById(id);
            assertNotNull(acct);
            int ip = acct.getIsPrimary();
            String ns = acct.getNamespace();
            StorageProviderType ty = acct.getStorageProviderType();
            assertNotNull(ip);
            assertNotNull(ns);
            assertNotNull(ty);

            // Save same StorageAcct with updates.
            StorageAcct acctNew = new StorageAcct();
            int ipNew = 1;
            String nsNew = ns + "test";
            StorageProviderType tyNew = StorageProviderType.AMAZON_S3;
            acctNew.setPrimary(ipNew);
            acctNew.setNamespace(nsNew);
            acctNew.setStorageProviderType(tyNew);

            // Setting the ID is how the update happens.
            acctNew.setId(id);
            repo.saveStorageAcct(acctNew);

            // Check updates.
            StorageAcct acctUpdated = repo.findStorageAcctById(id);
            assertNotNull(acctUpdated);
            assertEquals(ipNew, acctUpdated.getIsPrimary());
            assertEquals(nsNew, acctUpdated.getNamespace());
            assertEquals(tyNew, acctUpdated.getStorageProviderType());
        }
    }

    @Test
    public void testAnotherUpdate() throws Exception {
        verifyTableSize(0);
        int id = 1000;

        // Make sure StorageAcct not found with given id.
        StorageAcct acct = null;
        try {
            acct = repo.findStorageAcctById(id);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(acct == null);

        acct = new StorageAcct();
        acct.setId(id);
        acct.setPrimary(isPrimary);
        acct.setNamespace(namespace);
        acct.setStorageProviderType(providerType);

        repo.saveStorageAcct(acct);

        // Check that ID was ignored.
        verifyTableSize(1);
        StorageAcct acctFound = null;
        try {
            acctFound = repo.findStorageAcctById(id);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(acctFound == null);
    }

    @Test
    public void testEmptyUpdate() throws Exception {
        verifyTableSize(0);

        StorageAcct acct = new StorageAcct();

        // Should throw if on content.
        try {
            repo.saveStorageAcct(acct);
            fail("should have thrown exception.");
        } catch (Exception e1) {
        }

        verifyTableSize(0);
    }

    @Test
    public void testIsStorageNamespaceTaken() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        String testNamespace = "aJunkNamespaceForTesting";

        assertTrue(!repo.isStorageNamespaceTaken(testNamespace));

        // Add acct with namespace and test again.
        acctA.setNamespace(testNamespace);
        repo.saveStorageAcct(acctA);

        assertTrue(repo.isStorageNamespaceTaken(testNamespace));
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
            dbUtil.getOps().update("INSERT INTO " + tablename
                    + " (isPrimary,namespace,storageProviderType) VALUES ("
                    + isPrimary + ",'" + namespace + i + "','" + providerType
                    + i + "')");
        }
    }

    private void createTestDuraCloudAcct(String acctName) {
        dbUtil.getOps().update("INSERT INTO DuraCloudAcct "
                + " (accountName) VALUES (" + "'" + acctName + "')");

    }

    private int getTestDuraCloudAcctId(String acctName) {
        return dbUtil
                .getOps()
                .queryForInt("SELECT id FROM DuraCloudAcct WHERE accountName = ?",
                             new Object[] {acctName});
    }
}
