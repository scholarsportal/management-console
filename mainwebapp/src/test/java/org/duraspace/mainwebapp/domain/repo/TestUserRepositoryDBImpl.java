
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.DatabaseUtil;
import org.duraspace.mainwebapp.domain.model.User;
import org.duraspace.mainwebapp.domain.repo.db.MainDatabaseUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestUserRepositoryDBImpl {

    private final DatabaseUtil dbUtil;

    private final String baseDir = "testMainDB";

    private final Credential dbCred = new Credential("duraspace", "duraspace");

    private UserRepositoryDBImpl repo;

    private final String tablename =
            UserRepositoryDBImpl.getTableSpec().getTableName();

    private User userA;

    private final String lastname = "lastname";

    private final String firstname = "firstname";

    private final String email = "email";

    private final String phoneWork = "phoneWork";

    private final String phoneOther = "phoneOther";

    public TestUserRepositoryDBImpl()
            throws Exception {
        dbUtil = new MainDatabaseUtil(dbCred, baseDir);
        dbUtil.initializeDB();
    }

    @Before
    public void setUp() throws Exception {
        repo = new UserRepositoryDBImpl();
        repo.setDataSource(dbUtil.getDataSource());

        userA = new User();
        userA.setLastname(lastname);
        userA.setFirstname(firstname);
        userA.setEmail(email);
        userA.setPhoneWork(phoneWork);
        userA.setPhoneOther(phoneOther);

    }

    @After
    public void tearDown() throws Exception {
        repo = null;
        userA = null;
        dbUtil.clearDB();
    }

    @Test
    public void testSaveUser() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        int id = repo.saveUser(userA);

        verifyTableSize(4);

        User user = repo.findUserById(id);
        assertNotNull(user);
        assertEquals(lastname, user.getLastname());
        assertEquals(firstname, user.getFirstname());
        assertEquals(email, user.getEmail());
        assertEquals(phoneWork, user.getPhoneWork());
        assertEquals(phoneOther, user.getPhoneOther());

    }

    @Test
    public void testBadRetrieval() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<Integer> ids = repo.getUserIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        User user = null;
        try {
            user = repo.findUserById(-99);
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

        List<Integer> ids = repo.getUserIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        for (Integer id : ids) {
            // Get Address
            User user = repo.findUserById(id);
            assertNotNull(user);
            String ln = user.getLastname();
            String fn = user.getFirstname();
            String em = user.getEmail();
            String pw = user.getPhoneWork();
            String po = user.getPhoneOther();
            assertNotNull(ln);
            assertNotNull(fn);
            assertNotNull(em);
            assertNotNull(pw);
            assertNotNull(po);

            // Save same user with updates.
            User userNew = new User();
            String lnNew = ln + "test";
            String fnNew = fn + "test";
            String emNew = em + "test";
            String pwNew = pw + "test";
            String poNew = po + "test";
            userNew.setLastname(lnNew);
            userNew.setFirstname(fnNew);
            userNew.setEmail(emNew);
            userNew.setPhoneWork(pwNew);
            userNew.setPhoneOther(poNew);

            // Setting the ID is how the update happens.
            userNew.setId(id);
            repo.saveUser(userNew);

            // Check updates.
            User userUpdated = repo.findUserById(id);
            assertNotNull(userUpdated);
            assertEquals(lnNew, userUpdated.getLastname());
            assertEquals(fnNew, userUpdated.getFirstname());
            assertEquals(emNew, userUpdated.getEmail());
            assertEquals(pwNew, userUpdated.getPhoneWork());
            assertEquals(poNew, userUpdated.getPhoneOther());
        }
    }

    @Test
    public void testAnotherUpdate() throws Exception {
        verifyTableSize(0);
        int id = 1000;

        // Make sure user not found with given id.
        User user = null;
        try {
            user = repo.findUserById(id);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(user == null);

        user = new User();
        user.setId(id);
        user.setLastname(lastname);
        user.setFirstname(firstname);
        user.setEmail(email);
        user.setPhoneWork(phoneWork);
        user.setPhoneOther(phoneOther);

        repo.saveUser(user);

        // Check that ID was ignored.
        verifyTableSize(1);
        User userFound = null;
        try {
            userFound = repo.findUserById(id);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(userFound == null);
    }

    @Test
    public void testEmptyUpdate() throws Exception {
        verifyTableSize(0);

        User user = new User();

        // Should throw if on content.
        try {
            repo.saveUser(user);
            fail("should have thrown exception.");
        } catch (Exception e1) {
        }

        verifyTableSize(0);
    }

    @Test
    public void testFindUserByDuraCredId() throws Exception {
        int credId = insertTestCredential(new Credential("userX", "pwordX"));
        assertTrue(credId > 0);

        // Add User with created cred id.
        userA.setCredentialId(credId);
        repo.saveUser(userA);

        // Execute method under test.
        User userFound = repo.findUserByDuraCredId(credId);
        assertNotNull(userFound);

        assertEquals(credId, userFound.getCredentialId());
        assertEquals(lastname, userFound.getLastname());

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
                            + " (lastname,firstname,email,phoneWork,phoneOther) VALUES ("
                            + "'" + lastname + i + "','" + firstname + i
                            + "','" + email + i + "','" + phoneWork + i + "','"
                            + phoneOther + i + "')");
        }
    }

    private int insertTestCredential(Credential cred) {
        insertTestAuthority(cred.getUsername());

        dbUtil
                .getOps()
                .update("INSERT INTO Credential (username,password,enabled) VALUES ('"
                        + cred.getUsername()
                        + "','"
                        + cred.getPassword()
                        + "',1)");
        int id =
                dbUtil
                        .getOps()
                        .queryForInt("SELECT id FROM Credential WHERE username='"
                                             + cred.getUsername()
                                             + "' AND password='"
                                             + cred.getPassword() + "'",
                                     null,
                                     null);

        return id;
    }

    private void insertTestAuthority(String username) {
        dbUtil.getOps()
                .update("INSERT INTO Authority (username,authority) VALUES ('"
                        + username + "','some-authority')");
    }
}
