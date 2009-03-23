
package org.duraspace.mainwebapp.domain.repo.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.DatabaseUtil;
import org.duraspace.mainwebapp.config.MainWebAppConfig;
import org.duraspace.mainwebapp.domain.repo.AddressRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.AuthorityRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.ComputeAcctRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.ComputeProviderRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.CredentialRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.DuraSpaceAcctRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.StorageAcctRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.StorageProviderRepositoryDBImpl;
import org.duraspace.mainwebapp.domain.repo.UserRepositoryDBImpl;
import org.duraspace.serviceprovider.domain.ComputeProviderType;

import static org.junit.Assert.assertEquals;

public class MainDatabaseLoaderTest {

    private final DatabaseUtil dbUtil;

    private MainDatabaseLoader loader;

    public MainDatabaseLoaderTest()
            throws Exception {
        Credential dbCred = MainWebAppConfig.getDbCredential();

        String dbName = "target/testMainDB";
        dbUtil = new MainDatabaseUtil(dbCred, dbName);
        dbUtil.initializeDB();
    }

    @Before
    public void setUp() throws Exception {
        MainDatabaseLoaderFactory loaderFactory =
                new MainDatabaseLoaderFactory(dbUtil);
        loader = loaderFactory.getMainDatabaseLoader();
    }

    @After
    public void tearDown() throws Exception {

        loader = null;

        dbUtil.clearDB();
    }

    @Test
    public void testLoadTestData() throws Exception {
        verifyTablesEmpty();

        loader.loadTestData();

        verifyTablesPopulated();
    }

    private void verifyTablesEmpty() {

        verifyRowCount(AddressRepositoryDBImpl.getTableSpec().getTableName(), 0);
        verifyRowCount(UserRepositoryDBImpl.getTableSpec().getTableName(), 0);
        verifyRowCount(AuthorityRepositoryDBImpl.getTableSpec().getTableName(),
                       0);
        verifyRowCount(CredentialRepositoryDBImpl.getTableSpec().getTableName(),
                       0);
        verifyRowCount(DuraSpaceAcctRepositoryDBImpl.getTableSpec()
                .getTableName(), 0);
        verifyRowCount(ComputeProviderRepositoryDBImpl.getTableSpec()
                .getTableName(), 0);
        verifyRowCount(ComputeAcctRepositoryDBImpl.getTableSpec()
                .getTableName(), 0);
        verifyRowCount(StorageProviderRepositoryDBImpl.getTableSpec()
                .getTableName(), 0);
        verifyRowCount(StorageAcctRepositoryDBImpl.getTableSpec()
                .getTableName(), 0);

    }

    private void verifyTablesPopulated() {
        verifyRowCount(AddressRepositoryDBImpl.getTableSpec().getTableName(), 6);
        verifyRowCount(UserRepositoryDBImpl.getTableSpec().getTableName(), 6);
        verifyRowCount(AuthorityRepositoryDBImpl.getTableSpec().getTableName(),
                       7);
        verifyRowCount(CredentialRepositoryDBImpl.getTableSpec().getTableName(),
                       7);
        verifyRowCount(DuraSpaceAcctRepositoryDBImpl.getTableSpec()
                .getTableName(), 3);
        verifyRowCount(ComputeProviderRepositoryDBImpl.getTableSpec()
                .getTableName(), ComputeProviderType.values().length);
        verifyRowCount(ComputeAcctRepositoryDBImpl.getTableSpec()
                .getTableName(), 3);
        verifyRowCount(StorageProviderRepositoryDBImpl.getTableSpec()
                .getTableName(), 4);
        verifyRowCount(StorageAcctRepositoryDBImpl.getTableSpec()
                .getTableName(), 3);
    }

    private void verifyRowCount(String tablename, int count) {
        String query = "SELECT COUNT(*) FROM " + tablename;
        int numRows = dbUtil.getOps().queryForInt(query);
        assertEquals(tablename, count, numRows);
    }

}
