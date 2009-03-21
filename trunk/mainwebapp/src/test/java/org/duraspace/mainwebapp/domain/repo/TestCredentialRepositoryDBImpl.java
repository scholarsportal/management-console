
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.DatabaseUtil;
import org.duraspace.mainwebapp.domain.repo.db.MainDatabaseUtil;
import org.springframework.dao.DataAccessException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestCredentialRepositoryDBImpl {

    private final DatabaseUtil dbUtil;

    private final String baseDir = "testMainDB";

    private final Credential dbCred = new Credential("duraspace", "duraspace");

    private CredentialRepositoryDBImpl repo;

    private final String tablename =
            CredentialRepositoryDBImpl.getTableSpec().getTableName();

    private Credential credA;

    private final String username = "username";

    private final String password = "password";

    private final int enabled = 0;

    public TestCredentialRepositoryDBImpl()
            throws Exception {
        dbUtil = new MainDatabaseUtil(dbCred, baseDir);
        dbUtil.initializeDB();
    }

    @Before
    public void setUp() throws Exception {
        repo = new CredentialRepositoryDBImpl();
        repo.setDataSource(dbUtil.getDataSource());

        credA = new Credential();
        credA.setUsername(username);
        credA.setPassword(password);
        credA.setEnabled(enabled);

        // For foreign keys, add needed 'authority's
        insertTestAuthority(username);
        insertTestAuthority(username + "0test");
        insertTestAuthority(username + "1test");
    }

    @After
    public void tearDown() throws Exception {
        repo = null;
        credA = null;
        dbUtil.clearDB();
    }

    @Test
    public void testSaveCredential() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        int id = repo.saveCredential(credA);

        verifyTableSize(4);

        Credential cred = repo.findCredentialById(id);
        assertNotNull(cred);
        assertEquals(username, cred.getUsername());
        assertEquals(password, cred.getPassword());
        assertEquals(enabled, cred.getIsEnabled().intValue());

    }

    @Test
    public void testSaveWithPartialCredential() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        Credential cred = new Credential();
        cred.setUsername(username);
        cred.setPassword(null);
        cred.setEnabled(0);
        try {
            repo.saveCredential(cred);
            fail("Should throw exception since 'password' is required.");
        } catch (Exception e) {
        }

        verifyTableSize(3);
    }

    @Test
    public void testFindIdFor() throws Exception {
        verifyTableSize(0);

        Credential cred = new Credential();
        cred.setUsername(username);
        cred.setPassword(password);
        cred.setEnabled(enabled);
        repo.saveCredential(cred);

        verifyTableSize(1);

        int id = repo.findIdFor(cred);
        assertTrue(id > 0);

    }

    @Test
    public void testFindCredentialForUsername() throws Exception {
        verifyTableSize(0);

        String usernameGood = "usernameGood";
        String usernameBad = "usernameBad";

        insertTestAuthority(usernameGood);
        insertTestAuthority(usernameBad); // <-- this is not necessary

        Credential cred0 = new Credential();
        cred0.setUsername(usernameGood);
        cred0.setPassword(password);
        cred0.setEnabled(enabled);
        repo.saveCredential(cred0);

        verifyTableSize(1);

        try {
            repo.findCredentialByUsername(usernameBad);
            fail("Should throw exception.");
        } catch (Exception e) {
        }

        Credential credFound = repo.findCredentialByUsername(usernameGood);
        assertNotNull(credFound);
        assertEquals(usernameGood, credFound.getUsername());
        assertEquals(password, credFound.getPassword());
        assertEquals(enabled, credFound.getIsEnabled().intValue());

    }

    @Test
    public void testBadRetrieval() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<Integer> ids = repo.getCredentialIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        Credential cred = null;
        try {
            cred = repo.findCredentialById(-99);
            Assert.fail("Should throw exception.");
        } catch (Exception e) {
        }
        assertTrue(cred == null);
    }

    @Test
    public void testUpdates() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<Integer> ids = repo.getCredentialIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        for (Integer id : ids) {
            // Get Credential
            Credential cred = repo.findCredentialById(id);
            assertNotNull(cred);
            String un = cred.getUsername();
            String pw = cred.getPassword();
            int en = cred.getIsEnabled();
            assertNotNull(un);
            assertNotNull(pw);
            assertNotNull(en);

            // Save same Credential with updates.
            Credential credNew = new Credential();
            String unNew = un + "test";
            String pwNew = pw + "test";
            int enNew = 0;
            credNew.setUsername(unNew);
            credNew.setPassword(pwNew);
            credNew.setEnabled(enNew);

            // Setting the ID is how the update happens.
            credNew.setId(id);
            repo.saveCredential(credNew);

            // Check updates.
            Credential credUpdated = repo.findCredentialById(id);
            assertNotNull(credUpdated);
            assertEquals(unNew, credUpdated.getUsername());
            assertEquals(pwNew, credUpdated.getPassword());
            assertEquals(enNew, credUpdated.getIsEnabled().intValue());
        }
    }

    @Test
    public void testPartialUpdate() throws Exception {
        verifyTableSize(0);

        // Insert initial item
        Credential cred = new Credential();
        cred.setUsername(username);
        cred.setPassword(password);
        cred.setEnabled(enabled);
        int id = repo.saveCredential(cred);

        verifyTableSize(1);

        // Verify item.
        Credential credFound = repo.findCredentialById(id);
        assertNotNull(credFound);
        assertEquals(username, credFound.getUsername());
        assertEquals(password, credFound.getPassword());
        assertEquals(enabled, credFound.getIsEnabled().intValue());

        // Do partial update
        String passwordNew = "passwordNew";
        Credential credUpdated = new Credential();
        credUpdated.setId(id);
        credUpdated.setPassword(passwordNew);

        // Push update.
        repo.saveCredential(credUpdated);

        // Verify updates.
        Credential credVerify = repo.findCredentialById(id);
        assertNotNull(credVerify);
        assertEquals(username, credVerify.getUsername());
        assertEquals(passwordNew, credVerify.getPassword());
        assertEquals(1, credVerify.getIsEnabled().intValue());
    }

    @Test
    public void testInsert() throws Exception {
        verifyTableSize(0);
        int id = 1000;

        // Make sure Credential not found with given id.
        Credential cred = null;
        try {
            cred = repo.findCredentialById(id);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(cred == null);

        cred = new Credential();
        cred.setId(id);
        cred.setUsername(username);
        cred.setPassword(password);
        cred.setEnabled(enabled);
        repo.saveCredential(cred);

        // Check that ID was ignored.
        verifyTableSize(1);
        Credential credFound = null;
        try {
            credFound = repo.findCredentialById(id);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(credFound == null);
    }

    @Test
    public void testEmptyInsert() throws Exception {
        verifyTableSize(0);

        Credential cred = new Credential();

        // Should throw if on content.
        try {
            repo.saveCredential(cred);
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
            insertTestAuthority(username + i);
            dbUtil.getOps().update("INSERT INTO " + tablename
                    + " (username,password,enabled) VALUES (" + "'" + username
                    + i + "','" + password + i + "'," + enabled + ")");
        }
    }

    private void insertTestAuthority(String username) {
        try {
            dbUtil
                    .getOps()
                    .update("INSERT INTO Authority (username,authority) VALUES ('"
                            + username + "','some-authority')");
        } catch (DataAccessException e) {
        }
    }

}
