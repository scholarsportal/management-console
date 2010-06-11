package org.duracloud.mainwebapp.domain.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.duracloud.common.model.Credential;
import org.duracloud.common.util.TableSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class CredentialRepositoryDBImpl
        extends SimpleJdbcDaoSupport
        implements CredentialRepository {

    protected final Logger log = LoggerFactory.getLogger(CredentialRepositoryDBImpl.class);

    private final static String tablename = "Credential";

    private final static String idCol = "id";

    private final static String usernameCol = "username";

    private final static String passwordCol = "password";

    private final static String enabledCol = "enabled";

    private final String CREDENTIAL_INSERT =
            "INSERT INTO " + tablename + " (" + usernameCol + "," + passwordCol
                    + "," + enabledCol + ") " + "VALUES (:" + usernameCol
                    + ",:" + passwordCol + ",:" + enabledCol + ")";

    private final String CREDENTIAL_SELECT =
            "SELECT " + idCol + "," + usernameCol + "," + passwordCol + ","
                    + enabledCol + " FROM " + tablename;

    private final String CREDENTIAL_SELECT_BY_USERNAME =
            CREDENTIAL_SELECT + " WHERE " + usernameCol + " = ?";

    private final String CREDENTIAL_SELECT_BY_USERNAME_PASSWORD =
            "SELECT " + idCol + " FROM " + tablename + " WHERE " + usernameCol
                    + " = ? AND " + passwordCol + " = ?";

    private final String CREDENTIAL_SELECT_BY_ID =
            CREDENTIAL_SELECT + " WHERE " + idCol + " = ?";

    private final String ID_SELECT = "SELECT " + idCol + " FROM " + tablename;

    // Leaving DDL hard-coded for legibility.
    private final static String ddl =
            "CREATE TABLE Credential ("
                    + "id INT GENERATED ALWAYS AS IDENTITY,"
                    + "username VARCHAR(64) NOT NULL,"
                    + "password VARCHAR(64) NOT NULL,"
                    + "enabled INTEGER NOT NULL DEFAULT 1,"
                    + "UNIQUE (username),"
                    + "FOREIGN KEY (username) REFERENCES Authority (username),"
                    + "PRIMARY KEY (id))";

    /**
     * <pre>
     * This method persists the provided Credential.
     * If the provided Credential contains an ID,
     * ...the existing Credential will be over-written;
     * ...otherwise the provided Credential will be a new row.
     *
     * Note: If provided Credential contains an ID that does not exist
     * in the table, the ID will be ignored.
     * </pre> {@inheritDoc}
     *
     * @throws Exception
     */
    public int saveCredential(Credential cred) throws Exception {
        if (existsInTable(cred)) {
            return doUpdate(cred);
        } else {
            return doInsert(cred);
        }
    }

    private boolean existsInTable(Credential cred) {
        Credential existingCred = null;
        try {
            existingCred = findCredentialById(findIdFor(cred));
        } catch (Exception e) {
        }
        return existingCred != null;
    }

    @SuppressWarnings("unchecked")
    private int doUpdate(Credential cred) throws Exception {
        StringBuilder sb = new StringBuilder("UPDATE " + tablename + " SET ");
        int len = sb.length();
        if (cred.getUsername() != null) {
            sb.append(usernameCol + " = '" + cred.getUsername() + "', ");
        }
        if (cred.getPassword() != null) {
            sb.append(passwordCol + " = '" + cred.getPassword() + "', ");
        }
        if (cred.getIsEnabled() != null) {
            sb.append(enabledCol + " = " + cred.getIsEnabled() + ", ");
        }

        // Verify need for update.
        if (sb.length() == len) {
            throw new Exception("Nothing to update, all fields are null!");
        }

        sb.deleteCharAt(sb.lastIndexOf(", "));
        sb.append("WHERE " + idCol + " = " + cred.getId() + "");

        log.debug("Executing update: '" + sb.toString() + "'");
        this.getSimpleJdbcTemplate().update(sb.toString(), new HashMap());

        return findIdFor(cred);
    }

    private int doInsert(Credential cred) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(usernameCol, cred.getUsername());
        params.put(passwordCol, cred.getPassword());
        params.put(enabledCol, cred.getIsEnabled());

        this.getSimpleJdbcTemplate().update(CREDENTIAL_INSERT, params);

        return findIdFor(cred);
    }

    /**
     * <pre>
     * This method returns the Credential of the provided ID.
     * If no Credential is found, an exception is thrown.
     * </pre> {@inheritDoc}
     */
    public Credential findCredentialById(int id) throws Exception {
        List<Credential> credentials =
                this.getSimpleJdbcTemplate().query(CREDENTIAL_SELECT_BY_ID,
                                                   new CredentialRowMapper(),
                                                   id);
        if (credentials.size() != 1) {
            throw new Exception("A single Credential was not found for ID: "
                    + id);
        }

        return credentials.get(0);
    }

    /**
     * <pre>
     * This method returns all IDs in the table.
     * If no results are found, an exception is thrown.
     * </pre> {@inheritDoc}
     */
    public List<Integer> getCredentialIds() throws Exception {
        List<Integer> ids =
                this.getSimpleJdbcTemplate()
                        .query(ID_SELECT,
                               new ParameterizedRowMapper<Integer>() {

                                   public Integer mapRow(ResultSet rs,
                                                         int rowNum)
                                           throws SQLException {
                                       return new Integer(rs.getString(idCol));
                                   }
                               });
        if (ids.size() == 0) {
            throw new Exception("Table is empty: '" + tablename + "'");
        }
        return ids;
    }

    public Credential findCredentialByUsername(String username)
            throws Exception {
        List<Credential> credentials =
                this.getSimpleJdbcTemplate()
                        .query(CREDENTIAL_SELECT_BY_USERNAME,
                               new CredentialRowMapper(),
                               username);
        if (credentials.size() == 0) {
            throw new Exception("ID not found for: '" + username + "'");
        }
        return credentials.get(0);
    }

    /**
     * {@inheritDoc}
     */
    public int findIdFor(Credential cred) throws Exception {
        if (cred.hasId()) {
            return cred.getId();
        }
        List<Integer> ids =
                this.getSimpleJdbcTemplate()
                        .query(CREDENTIAL_SELECT_BY_USERNAME_PASSWORD,
                               new ParameterizedRowMapper<Integer>() {

                                   public Integer mapRow(ResultSet rs,
                                                         int rowNum)
                                           throws SQLException {
                                       return new Integer(rs.getInt(idCol));

                                   }
                               },
                               cred.getUsername(),
                               cred.getPassword());
        if (ids.size() == 0) {
            throw new Exception("ID not found for: '" + cred + "'");
        }
        return ids.get(0);
    }

    private class CredentialRowMapper
            implements ParameterizedRowMapper<Credential> {

        public Credential mapRow(ResultSet rs, int rowNum) throws SQLException {
            Credential credential = new Credential();
            credential.setId(rs.getInt(idCol));
            credential.setUsername(rs.getString(usernameCol));
            credential.setPassword(rs.getString(passwordCol));
            credential.setEnabled(rs.getInt(enabledCol));
            return credential;

        }
    }

    public static TableSpec getTableSpec() {
        TableSpec ts = new TableSpec();
        ts.setTableName(tablename);
        ts.setPrimaryKey(idCol);
        ts.setDdl(ddl);
        return ts;
    }

}
