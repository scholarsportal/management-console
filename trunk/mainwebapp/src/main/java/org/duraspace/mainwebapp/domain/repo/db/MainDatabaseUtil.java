
package org.duraspace.mainwebapp.domain.repo.db;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.DatabaseUtil;
import org.duraspace.common.util.ExceptionUtil;
import org.duraspace.common.util.TableSpec;
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

public class MainDatabaseUtil
        extends DatabaseUtil {

    protected static final Logger log =
            Logger.getLogger(MainDatabaseUtil.class);

    private final List<TableSpec> tableSpecs =
            Arrays.asList(AddressRepositoryDBImpl.getTableSpec(),
                          AuthorityRepositoryDBImpl.getTableSpec(),
                          CredentialRepositoryDBImpl.getTableSpec(),
                          DuraSpaceAcctRepositoryDBImpl.getTableSpec(),
                          ComputeProviderRepositoryDBImpl.getTableSpec(),
                          StorageProviderRepositoryDBImpl.getTableSpec(),
                          ComputeAcctRepositoryDBImpl.getTableSpec(),
                          StorageAcctRepositoryDBImpl.getTableSpec(),
                          UserRepositoryDBImpl.getTableSpec());

    public MainDatabaseUtil(Credential cred, String baseDir) {
        super(cred, baseDir);
    }

    @Override
    protected List<TableSpec> getTableSpecs() {
        return tableSpecs;
    }

    protected static void ensureExistsDB() {

        try {
            String derbyHome = MainWebAppConfig.getDbHome();
            System.setProperty("derby.system.home", derbyHome);

            Credential cred = MainWebAppConfig.getDbCredential();
            String databaseName = MainWebAppConfig.getDbName();
            MainDatabaseUtil dbUtil = new MainDatabaseUtil(cred, databaseName);

            dbUtil.ensureDatabaseExists();
            dbUtil.ensureTablesExist();

            log.info("load test data? " + MainWebAppConfig.getDbLoadTestData());
            if (MainWebAppConfig.getDbLoadTestData()) {
                log.info("Loading database with test data.");
                MainDatabaseLoader loader = new MainDatabaseLoader();
                loader.loadTestData();
            }
        } catch (Exception e) {
            log.error("Unable to load application properties: "
                    + e.getMessage());
            log.error(ExceptionUtil.getStackTraceAsString(e));
        }
    }

    private static void usage() {
        StringBuilder sb = new StringBuilder("Usage:");
        sb.append("\n\tMainDatabaseUtil " +
        		"<location-of-database> <accessKeyId> <secretAccessKey>");
        sb.append("\n\n\tWhere <location-of-database> is the full path to ");
        sb.append("\n\tthe desired location of the populated database.");
        sb.append("\n\n\tWhere <accessKeyId> <secretAccessKey> are amazon");
        sb.append("\n\tcredentials. " +
        		"[The credential fields can be populated with junk as well.]");
        System.out.println(sb.toString());
    }

    public static void main(String[] args) {

        if (args.length != 3) {
            usage();
            System.exit(0);
        }
        String dbName = args[0];
        if ("x".equals(dbName)) {
            dbName = "/opt/derby/duraspaceDB";
        }

        String accessKey = args[1];
        String secret = args[2];
        Credential amazonCred = new Credential(accessKey, secret);

        try {
            Credential dbCred = MainWebAppConfig.getDbCredential();
            MainDatabaseUtil dbUtil = new MainDatabaseUtil(dbCred, dbName);
            dbUtil.initializeDB();

            MainDatabaseLoader loader =
                    new MainDatabaseLoaderFactory(dbUtil, amazonCred)
                            .getMainDatabaseLoader();
            loader.loadTestData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
