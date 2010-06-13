/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo.db;

import java.util.Arrays;
import java.util.List;

import org.duracloud.common.model.Credential;
import org.duracloud.common.util.DatabaseUtil;
import org.duracloud.common.util.ExceptionUtil;
import org.duracloud.common.util.TableSpec;
import org.duracloud.mainwebapp.config.MainWebAppConfig;
import org.duracloud.mainwebapp.domain.repo.AddressRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.AuthorityRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.ComputeAcctRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.ComputeProviderRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.CredentialRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.DuraCloudAcctRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.StorageAcctRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.StorageProviderRepositoryDBImpl;
import org.duracloud.mainwebapp.domain.repo.UserRepositoryDBImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainDatabaseUtil
        extends DatabaseUtil {

    protected static final Logger log =
            LoggerFactory.getLogger(MainDatabaseUtil.class);

    private final List<TableSpec> tableSpecs =
            Arrays.asList(AddressRepositoryDBImpl.getTableSpec(),
                          AuthorityRepositoryDBImpl.getTableSpec(),
                          CredentialRepositoryDBImpl.getTableSpec(),
                          DuraCloudAcctRepositoryDBImpl.getTableSpec(),
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
            dbName = "/opt/derby/duracloudDB";
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
