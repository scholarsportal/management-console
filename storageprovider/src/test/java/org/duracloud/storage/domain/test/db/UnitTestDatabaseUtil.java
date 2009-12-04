package org.duracloud.storage.domain.test.db;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.duracloud.common.model.Credential;
import org.duracloud.common.util.DatabaseUtil;
import org.duracloud.common.util.TableSpec;
import org.duracloud.storage.domain.StorageProviderType;

import java.util.Arrays;
import java.util.List;

public class UnitTestDatabaseUtil
        extends DatabaseUtil {

    protected static final Logger log =
            Logger.getLogger(UnitTestDatabaseUtil.class);

    private final List<TableSpec> tableSpecs =
            Arrays.asList(PasswordRepositoryDBImpl.getTableSpec());

    PasswordRepositoryDBImpl repo;

    public UnitTestDatabaseUtil()
            throws Exception {
        this(getDatabaseCredential(), getDatabaseHome(), getBootPassword());
    }

    public UnitTestDatabaseUtil(Credential cred,
                                String baseDir,
                                String bootPassword)
            throws Exception {
        super(cred, baseDir, bootPassword);
        if (!isValid(baseDir, bootPassword)) {
            log.error("unit.database.home='" + baseDir + "'");
            log.error("unit.database.password='" + bootPassword + "'");
            throw new Exception(usage());
        }
        repo = createRepo();
    }

    private PasswordRepositoryDBImpl createRepo() {
        PasswordRepositoryDBImpl repo = new PasswordRepositoryDBImpl();
        repo.setDataSource(getDataSource());
        return repo;
    }

    @Override
    protected List<TableSpec> getTableSpecs() {
        return tableSpecs;
    }

    public void createNewDB() throws Exception {
        initializeDB();
    }

    public void connectToExistingDB() throws Exception {
        ensureDatabaseExists();
        ensureTablesExist();
    }

    public void insertCredentialForProvider(StorageProviderType type,
                                            Credential cred) {
        repo.insertPassword(type, cred.getUsername(), cred.getPassword());
    }

    public Credential findCredentialForProvider(StorageProviderType providerType)
            throws Exception {
        return repo.findCredentialByProviderType(providerType);
    }

    public static boolean isValid(String... texts) {
        boolean valid = true;
        for (String text : texts) {
            if (StringUtils.isBlank(text)) {
                valid = false;
            }
        }
        return valid;
    }

    private static Credential getDatabaseCredential() {
        return new Credential("duracloud", "duracloud");
    }

    private static String getBootPassword() {
        return System.getProperty("unit.database.password");
    }

    private static String getDatabaseHome() {
        return System.getProperty("unit.database.home");
    }

    public static String usage() {
        StringBuilder sb =
                new StringBuilder("\n----------------------------\n");
        sb.append("Usage:");
        sb.append("\n\tUnitTestDatabaseUtil ");
        sb.append("-Dunit.database.password=<boot-password> ");
        sb.append("-Dunit.database.home=<location-of-database>");
        sb.append("\n\n\tWhere <boot-password> is the password to ");
        sb.append("boot the encrypted database.");
        sb.append("\n\n\tAnd <location-of-database> is the full path to ");
        sb.append("\n\tthe desired location of the populated database.");
        sb.append("\n\n\tNOTE: When running storage-provider unit tests ");
        sb.append("in maven,");
        sb.append("\n\tthe maven settings.xml should hold the ");
        sb.append("unit.database.password and unit.database.home.");
        sb.append("\n\tSee notes in resources/install-notes.txt");
        sb.append("\n----------------------------\n");
        return sb.toString();
    }

}
