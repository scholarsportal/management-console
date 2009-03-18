
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.DatabaseUtil;
import org.duraspace.mainwebapp.domain.model.Authority;
import org.duraspace.mainwebapp.domain.repo.db.MainDatabaseUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestAuthorityRepositoryDBImpl {

    private final DatabaseUtil dbUtil;

    private final String baseDir = "testMainDB";

    private final Credential dbCred = new Credential("duraspace", "duraspace");

    private AuthorityRepositoryDBImpl repo;

    private final String tablename =
            AuthorityRepositoryDBImpl.getTableSpec().getTableName();

    private Authority authA;

    private final String username = "username";

    private final String authority = "authority";

    public TestAuthorityRepositoryDBImpl()
            throws Exception {
        dbUtil = new MainDatabaseUtil(dbCred, baseDir);
        dbUtil.initializeDB();
    }

    @Before
    public void setUp() throws Exception {
        repo = new AuthorityRepositoryDBImpl();
        repo.setDataSource(dbUtil.getDataSource());

        authA = new Authority();
        authA.setUsername(username);
        authA.setAuthority(authority);
    }

    @After
    public void tearDown() throws Exception {
        repo = null;
        authA = null;
        dbUtil.clearDB();
    }

    @Test
    public void testSaveAuthority() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        repo.saveAuthority(authA);

        verifyTableSize(4);

        List<String> unames = repo.getAuthorityUsernames();
        assertNotNull(unames);
        assertTrue(unames.size() == 4);

        boolean found = false;
        for (String uname : unames) {
            Authority auth = repo.findAuthorityByUsername(uname);
            assertNotNull(auth);

            if (username.equals(auth.getUsername())
                    && authority.equals(auth.getAuthority())) {
                found = true;
            }
        }
        assertTrue(found);

    }

    @Test
    public void testSaveWithPartialAuthority() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        Authority auth = new Authority();
        auth.setUsername(username);

        try {
            repo.saveAuthority(auth);
            fail("Should throw exception since all fields required.");
        } catch (Exception e) {
        }

        verifyTableSize(3);

    }

    @Test
    public void testBadRetrieval() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<String> ids = repo.getAuthorityUsernames();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        Authority auth = null;
        try {
            auth = repo.findAuthorityByUsername("junk-username");
            Assert.fail("Should throw exception.");
        } catch (Exception e) {
        }
        assertTrue(auth == null);
    }

    @Test
    public void testUpdates() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<String> unames = repo.getAuthorityUsernames();
        assertNotNull(unames);
        assertTrue(unames.size() == 2);

        for (String uname : unames) {
            // Get Authority
            Authority auth = repo.findAuthorityByUsername(uname);
            assertNotNull(auth);
            String un = auth.getUsername();
            String au = auth.getAuthority();
            assertNotNull(un);
            assertNotNull(au);

            // Save same authority with updates.
            Authority authNew = new Authority();
            String unNew = un + "test";
            String auNew = au + "test";
            authNew.setUsername(unNew);
            authNew.setAuthority(auNew);

            // Setting the Username is how the update happens.
            authNew.setUsername(uname);
            repo.saveAuthority(authNew);

            // Check updates.
            Authority authUpdated = repo.findAuthorityByUsername(uname);
            assertNotNull(authUpdated);
            assertEquals(uname, authUpdated.getUsername());
            assertEquals(auNew, authUpdated.getAuthority());
        }
    }

    @Test
    public void testInsert() throws Exception {
        verifyTableSize(0);
        String uname = "user1";

        // Make sure Authority not found with given username.
        Authority auth = null;
        try {
            auth = repo.findAuthorityByUsername(uname);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(auth == null);

        auth = new Authority();
        auth.setUsername(uname);
        auth.setAuthority(authority);
        repo.saveAuthority(auth);

        // Check that Username was changed.
        verifyTableSize(1);
        Authority authFound = null;
        authFound = repo.findAuthorityByUsername(uname);
        assertNotNull(authFound);
    }

    @Test
    public void testEmptyInsert() throws Exception {
        verifyTableSize(0);

        Authority auth = new Authority();

        // Should throw if no content.
        try {
            repo.saveAuthority(auth);
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
                    + " (username,authority) VALUES (" + "'" + username + i
                    + "','" + authority + i + "')");
        }
    }

}
