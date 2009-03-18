
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.DatabaseUtil;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
import org.duraspace.mainwebapp.domain.repo.db.MainDatabaseUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestDuraSpaceAcctRepositoryDBImpl {

    private final DatabaseUtil dbUtil;

    private final String baseDir = "testMainDB";

    private final Credential dbCred = new Credential("duraspace", "duraspace");

    private DuraSpaceAcctRepositoryDBImpl repo;

    private final String tablename =
            DuraSpaceAcctRepositoryDBImpl.getTableSpec().getTableName();

    private DuraSpaceAcct duraAcctA;

    private final String accountName = "accountName";

    private final int billingInfoId = 111;

    public TestDuraSpaceAcctRepositoryDBImpl()
            throws Exception {
        dbUtil = new MainDatabaseUtil(dbCred, baseDir);
        dbUtil.initializeDB();
    }

    @Before
    public void setUp() throws Exception {
        repo = new DuraSpaceAcctRepositoryDBImpl();
        repo.setDataSource(dbUtil.getDataSource());

        duraAcctA = new DuraSpaceAcct();
        duraAcctA.setAccountName(accountName);
        duraAcctA.setBillingInfoId(billingInfoId);

    }

    @After
    public void tearDown() throws Exception {
        repo = null;
        duraAcctA = null;
        dbUtil.clearDB();
    }

    @Test
    public void testSaveDuraAcct() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        int id = repo.saveDuraAcct(duraAcctA);

        verifyTableSize(4);

        DuraSpaceAcct duraAcct = repo.findDuraAcctById(id);
        assertNotNull(duraAcct);
        assertEquals(accountName, duraAcct.getAccountName());
        assertEquals(billingInfoId, duraAcct.getBillingInfoId());

    }

    @Test
    public void testFindDuraAcctByName() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        String acctName = accountName + 1;
        DuraSpaceAcct acct = repo.findDuraAcctByName(acctName);
        assertNotNull(acct);
        assertEquals(acct.getAccountName(), acctName);
    }

    @Test
    public void testBadRetrieval() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<Integer> ids = repo.getDuraAcctIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        DuraSpaceAcct user = null;
        try {
            user = repo.findDuraAcctById(-99);
            Assert.fail("Should throw exception.");
        } catch (Exception e) {
        }
        assertTrue(user == null);
    }

    @Test
    public void testUpdates() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<Integer> ids = repo.getDuraAcctIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        for (Integer id : ids) {
            // Get DuraSpaceAcct
            DuraSpaceAcct duraAcct = repo.findDuraAcctById(id);
            assertNotNull(duraAcct);
            String an = duraAcct.getAccountName();
            int bi = duraAcct.getBillingInfoId();
            assertNotNull(an);
            assertTrue(bi > 0);

            // Save same acct with updates.
            DuraSpaceAcct duraAcctNew = new DuraSpaceAcct();
            String anNew = an + "test";
            int biNew = bi + 1000;
            duraAcctNew.setAccountName(anNew);
            duraAcctNew.setBillingInfoId(biNew);

            // Setting the ID is how the update happens.
            duraAcctNew.setId(id);
            repo.saveDuraAcct(duraAcctNew);

            // Check updates.
            DuraSpaceAcct duraAcctUpdated = repo.findDuraAcctById(id);
            assertNotNull(duraAcctUpdated);
            assertEquals(anNew, duraAcctUpdated.getAccountName());
            assertEquals(biNew, duraAcctUpdated.getBillingInfoId());
        }
    }

    @Test
    public void testAnotherUpdate() throws Exception {
        verifyTableSize(0);
        int id = 1000;

        // Make sure acct not found with given id.
        DuraSpaceAcct duraAcct = null;
        try {
            duraAcct = repo.findDuraAcctById(id);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(duraAcct == null);

        duraAcct = new DuraSpaceAcct();
        duraAcct.setId(id);
        duraAcct.setAccountName(accountName);
        duraAcct.setBillingInfoId(billingInfoId);

        repo.saveDuraAcct(duraAcct);

        // Check that ID was ignored.
        verifyTableSize(1);
        DuraSpaceAcct duraAcctFound = null;
        try {
            duraAcctFound = repo.findDuraAcctById(id);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(duraAcctFound == null);
    }

    @Test
    public void testEmptyUpdate() throws Exception {
        verifyTableSize(0);

        DuraSpaceAcct duraAcct = new DuraSpaceAcct();

        // Should throw if on content.
        try {
            repo.saveDuraAcct(duraAcct);
            fail("should have thrown exception.");
        } catch (Exception e1) {
        }

        verifyTableSize(0);
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
                    + " (accountName,billingInfo_id) VALUES (" + "'"
                    + accountName + i + "'," + billingInfoId + i + ")");
        }
    }

}
