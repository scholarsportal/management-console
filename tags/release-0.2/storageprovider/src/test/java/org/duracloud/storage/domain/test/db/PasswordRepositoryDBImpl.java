package org.duracloud.storage.domain.test.db;

import org.duracloud.common.model.Credential;
import org.duracloud.common.util.TableSpec;
import org.duracloud.storage.domain.StorageProviderType;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PasswordRepositoryDBImpl
        extends SimpleJdbcDaoSupport {

    private static final String tablename = "passwords";

    private final static String idCol = "id";

    private static final String providerTypeCol = "providerType";

    private final static String usernameCol = "username";

    private final static String passwordCol = "password";

    private final String PASSWORD_INSERT =
            "INSERT INTO " + tablename + " (" + providerTypeCol + ", "
                    + usernameCol + ", " + passwordCol + ") " + "VALUES (:"
                    + providerTypeCol + ", :" + usernameCol + ", :"
                    + passwordCol + ")";

    private final String PASSWORD_SELECT =
            "SELECT " + passwordCol + " FROM " + tablename;

    private final String CREDENTIAL_SELECT_BY_PROVIDER_TYPE =
            "SELECT " + usernameCol + ", " + passwordCol + " FROM " + tablename
                    + " WHERE " + providerTypeCol + " = ? ";

    private final String PASSWORD_SELECT_BY_PROVIDER_TYPE_AND_USERNAME =
            PASSWORD_SELECT + " WHERE " + providerTypeCol + " = ? AND "
                    + usernameCol + " = ? ";

    private static final String ddl =
            "CREATE TABLE passwords (id INT GENERATED ALWAYS AS IDENTITY,"
                    + "providerType VARCHAR(32) NOT NULL,"
                    + "username VARCHAR(64) NOT NULL,"
                    + "password VARCHAR(64) NOT NULL)";

    public void insertPassword(StorageProviderType provider,
                               String username,
                               String password) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(providerTypeCol, provider.toString());
        params.put(usernameCol, username);
        params.put(passwordCol, password);

        this.getSimpleJdbcTemplate().update(PASSWORD_INSERT, params);
    }

    public Credential findCredentialByProviderType(StorageProviderType providerType)
            throws Exception {
        List<Credential> credentials =
                this.getSimpleJdbcTemplate()
                        .query(CREDENTIAL_SELECT_BY_PROVIDER_TYPE,
                               new ParameterizedRowMapper<Credential>() {

                                   public Credential mapRow(ResultSet rs,
                                                            int rowNum)
                                           throws SQLException {
                                       String username =
                                               rs.getString(usernameCol);
                                       String password =
                                               rs.getString(passwordCol);
                                       return new Credential(username, password);
                                   }
                               },
                               providerType.toString());
        if (credentials.size() == 0) {
            throw new Exception("Table is empty: '" + tablename + "'");
        }
        if (credentials.size() != 1) {
            throw new Exception(tablename
                    + " contains more than one entry for providerType: "
                    + providerType.toString());
        }

        return credentials.get(0);

    }

    public String findPasswordByProviderTypeAndUsername(StorageProviderType providerType,
                                                        String username)
            throws Exception {
        List<String> passwords =
                this.getSimpleJdbcTemplate()
                        .query(PASSWORD_SELECT_BY_PROVIDER_TYPE_AND_USERNAME,
                               new ParameterizedRowMapper<String>() {

                                   public String mapRow(ResultSet rs, int rowNum)
                                           throws SQLException {
                                       return rs.getString(passwordCol);
                                   }
                               },
                               providerType.toString(),
                               username);
        if (passwords.size() == 0) {
            throw new Exception("Table is empty: '" + tablename + "'");
        }
        if (passwords.size() != 1) {
            throw new Exception(tablename
                    + " contains more than one entry for providerType and username : ["
                    + providerType.toString() + "|" + username + "]");
        }

        return passwords.get(0);
    }

    public static TableSpec getTableSpec() {
        TableSpec ts = new TableSpec();
        ts.setTableName(tablename);
        ts.setPrimaryKey(idCol);
        ts.setDdl(ddl);
        return ts;
    }

}
