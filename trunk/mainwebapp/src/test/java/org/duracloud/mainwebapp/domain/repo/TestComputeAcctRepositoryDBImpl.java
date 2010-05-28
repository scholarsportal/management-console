package org.duracloud.mainwebapp.domain.repo;

import org.duracloud.common.model.Credential;
import org.duracloud.common.util.DatabaseUtil;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.mainwebapp.domain.model.ComputeAcct;
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

public class TestComputeAcctRepositoryDBImpl {

    private final DatabaseUtil dbUtil;

    private final String baseDir = "testMainDB";

    private final Credential dbCred = new Credential("duracloud", "duracloud");

    private ComputeAcctRepositoryDBImpl repo;

    private final String tablename =
            ComputeAcctRepositoryDBImpl.getTableSpec().getTableName();

    private ComputeAcct acctA;

    private final String namespace = "namespace";

    private final String instanceId = "instanceId";

    private final String computeProps = "computeProps";

    private final ComputeProviderType providerType =
            ComputeProviderType.MICROSOFT_AZURE;

    public TestComputeAcctRepositoryDBImpl()
            throws Exception {
        dbUtil = new MainDatabaseUtil(dbCred, baseDir);
        dbUtil.initializeDB();
    }

    @Before
    public void setUp() throws Exception {
        repo = new ComputeAcctRepositoryDBImpl();
        repo.setDataSource(dbUtil.getDataSource());

        acctA = new ComputeAcct();
        acctA.setNamespace(namespace);
        acctA.setInstanceId(instanceId);
        acctA.setXmlProps(computeProps);
        acctA.setComputeProviderType(providerType);

    }

    @After
    public void tearDown() throws Exception {
        repo = null;
        acctA = null;
        dbUtil.clearDB();
    }

    @Test
    public void testSaveComputeAcct() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        int id = repo.saveComputeAcct(acctA);

        verifyTableSize(4);

        ComputeAcct acct = repo.findComputeAcctById(id);
        assertNotNull(acct);
        assertEquals(namespace, acct.getNamespace());
        assertEquals(instanceId, acct.getInstanceId());
        assertEquals(computeProps, acct.getXmlProps());
        assertEquals(providerType, acct.getComputeProviderType());

    }

    @Test
    public void testBadRetrieval() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<Integer> ids = repo.getComputeAcctIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        ComputeAcct acct = null;
        try {
            acct = repo.findComputeAcctById(-99);
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

        List<Integer> ids = repo.getComputeAcctIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        for (Integer id : ids) {
            // Get ComputeAcct
            ComputeAcct acct = repo.findComputeAcctById(id);
            assertNotNull(acct);
            String ns = acct.getNamespace();
            String ii = acct.getInstanceId();
            String cp = acct.getXmlProps();
            ComputeProviderType ty = acct.getComputeProviderType();
            assertNotNull(ns);
            assertNotNull(ii);
            assertNotNull(cp);
            assertNotNull(ty);

            // Save same ComputeAcct with updates.
            ComputeAcct acctNew = new ComputeAcct();
            String nsNew = ns + "test";
            String iiNew = ii + "test";
            String cpNew = cp;
            ComputeProviderType tyNew = ComputeProviderType.RACKSPACE_CLOUDSERVERS;
            acctNew.setNamespace(nsNew);
            acctNew.setInstanceId(iiNew);
            acctNew.setXmlProps(cpNew);
            acctNew.setComputeProviderType(tyNew);

            // Setting the ID is how the update happens.
            acctNew.setId(id);
            repo.saveComputeAcct(acctNew);

            // Check updates.
            ComputeAcct acctUpdated = repo.findComputeAcctById(id);
            assertNotNull(acctUpdated);
            assertEquals(nsNew, acctUpdated.getNamespace());
            assertEquals(iiNew, acctUpdated.getInstanceId());
            assertEquals(cpNew, acctUpdated.getXmlProps());
            assertEquals(tyNew, acctUpdated.getComputeProviderType());
        }
    }

    @Test
    public void testAnotherUpdate() throws Exception {
        verifyTableSize(0);
        int id = 1000;

        // Make sure ComputeAcct not found with given id.
        ComputeAcct acct = null;
        try {
            acct = repo.findComputeAcctById(id);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(acct == null);

        acct = new ComputeAcct();
        acct.setId(id);
        acct.setNamespace(namespace);
        acct.setInstanceId(instanceId);
        acct.setXmlProps(computeProps);
        acct.setComputeProviderType(providerType);

        repo.saveComputeAcct(acct);

        // Check that ID was ignored.
        verifyTableSize(1);
        ComputeAcct acctFound = null;
        try {
            acctFound = repo.findComputeAcctById(id);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(acctFound == null);
    }

    @Test
    public void testEmptyUpdate() throws Exception {
        verifyTableSize(0);

        ComputeAcct acct = new ComputeAcct();

        // Should throw if on content.
        try {
            repo.saveComputeAcct(acct);
            fail("should have thrown exception.");
        } catch (Exception e1) {
        }

        verifyTableSize(0);
    }

    @Test
    public void testIsComputeNamespaceTaken() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        String testNamespace = "aJunkNamespaceForTesting";

        assertTrue(!repo.isComputeNamespaceTaken(testNamespace));

        // Add acct with namespace and test again.
        acctA.setNamespace(testNamespace);
        repo.saveComputeAcct(acctA);

        assertTrue(repo.isComputeNamespaceTaken(testNamespace));
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
            dbUtil
                    .getOps()
                    .update("INSERT INTO "
                            + tablename
                            + " (namespace,instanceId,computeProps,ComputeProviderType) VALUES ("
                            + "'" + namespace + i + "','" + instanceId + i
                            + "','" + computeProps + i + "','" + providerType
                            + i + "')");
        }
    }

}
