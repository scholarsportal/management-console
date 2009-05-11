
package org.duraspace.storage.domain.test.db;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.DatabaseUtil;
import org.duraspace.storage.domain.StorageProviderType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestPasswordRepositoryDBImpl {

    private final DatabaseUtil dbUtil;

    private final String baseDir = "testUnitDB";

    private final Credential dbCred = new Credential("duraspace", "duraspace");

    private final String bootPassword = "bxxtPassword";

    private PasswordRepositoryDBImpl repo;

    private final String tablename =
            PasswordRepositoryDBImpl.getTableSpec().getTableName();

    private final StorageProviderType provider = StorageProviderType.EMC;

    private final String username = "username";

    private final String password = "paxxword";

    public TestPasswordRepositoryDBImpl()
            throws Exception {
        dbUtil = new UnitTestDatabaseUtil(dbCred, baseDir, bootPassword);
        dbUtil.initializeDB();
    }

    @Before
    public void setUp() throws Exception {
        repo = new PasswordRepositoryDBImpl();
        repo.setDataSource(dbUtil.getDataSource());
    }

    @After
    public void tearDown() throws Exception {
        repo = null;
        dbUtil.clearDB();
    }

    @Test
    public void testFindPassword() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        repo.insertPassword(provider, username, password);

        verifyTableSize(4);

        String pword =
                repo.findPasswordByProviderTypeAndUsername(provider, username);
        assertNotNull(pword);
        assertEquals(pword, password);
    }

    @Test
    public void testFindCredential() throws Exception {
        verifyTableSize(0);
        repo.insertPassword(StorageProviderType.AMAZON_S3,
                            username + 0,
                            bootPassword + 0);
        repo.insertPassword(StorageProviderType.MICROSOFT_AZURE,
                            username + 1,
                            bootPassword + 1);
        repo.insertPassword(StorageProviderType.RACKSPACE,
                            username + 2,
                            bootPassword + 2);
        verifyTableSize(3);

        repo.insertPassword(provider, username, password);
        verifyTableSize(4);

        Credential cred = repo.findCredentialByProviderType(provider);
        assertNotNull(cred);
        assertEquals(cred.getUsername(), username);
        assertEquals(cred.getPassword(), password);
    }

    @Test
    public void testDuplicateFindCredential() throws Exception {
        verifyTableSize(0);
        repo.insertPassword(provider,
                            username + 0,
                            bootPassword + 0);
        repo.insertPassword(provider,
                            username + 1,
                            bootPassword + 1);
        verifyTableSize(2);

        Credential cred = null;
        try {
            cred = repo.findCredentialByProviderType(provider);
            Assert.fail("Should have thrown exception.");
        } catch (Exception e) {
        }
        assertEquals(cred, null);
    }

    @Test
    public void testMissingFindCredential() throws Exception {
        verifyTableSize(0);
        repo.insertPassword(StorageProviderType.AMAZON_S3,
                            username + 0,
                            bootPassword + 0);
        repo.insertPassword(StorageProviderType.MICROSOFT_AZURE,
                            username + 1,
                            bootPassword + 1);
        repo.insertPassword(StorageProviderType.RACKSPACE,
                            username + 2,
                            bootPassword + 2);
        verifyTableSize(3);

        Credential cred = null;
        try {
            cred = repo.findCredentialByProviderType(provider);
            Assert.fail("Should have thrown exception.");
        } catch (Exception e) {
        }
        assertEquals(cred, null);
    }

    @Test
    public void testBadRetrieval() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        String pword = null;
        try {
            pword =
                    repo.findPasswordByProviderTypeAndUsername(provider,
                                                               "bad-password");
            Assert.fail("Should throw exception.");
        } catch (Exception e) {
        }
        assertTrue(pword == null);
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
                    + " (providerType,username,password) VALUES (" + "'"
                    + provider + "','" + username + i + "','" + password + i
                    + "')");
        }
    }

}
