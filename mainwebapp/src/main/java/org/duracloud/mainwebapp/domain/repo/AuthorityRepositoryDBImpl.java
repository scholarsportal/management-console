package org.duracloud.mainwebapp.domain.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.duracloud.common.util.TableSpec;
import org.duracloud.mainwebapp.domain.model.Authority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class AuthorityRepositoryDBImpl
        extends SimpleJdbcDaoSupport
        implements AuthorityRepository {

    protected final Logger log = LoggerFactory.getLogger(AuthorityRepositoryDBImpl.class);

    private final static String tablename = "Authority";

    private final static String usernameCol = "username";

    private final static String authorityCol = "authority";

    private final String AUTHORITY_INSERT =
            "INSERT INTO " + tablename + " (" + usernameCol + ","
                    + authorityCol + ") " + "VALUES (:" + usernameCol + ",:"
                    + authorityCol + ")";

    private final String AUTHORITY_SELECT =
            "SELECT " + usernameCol + "," + authorityCol + " FROM " + tablename;

    private final String AUTHORITY_SELECT_BY_USERNAME =
            AUTHORITY_SELECT + " WHERE " + usernameCol + " = ?";

    private final String USERNAMES_SELECT =
            "SELECT " + usernameCol + " FROM " + tablename;

    // Leaving DDL hard-coded for legibility.
    private final static String ddl =
            "CREATE TABLE Authority (" + "username VARCHAR(64) NOT NULL,"
                    + "authority VARCHAR(64) NOT NULL,"
                    + "PRIMARY KEY (username))";

    /**
     * <pre>
     * This method persists the provided Authority.
     * If the provided Authority contains an ID,
     * ...the existing Authority will be over-written;
     * ...otherwise the provided Authority will be a new row.
     *
     * Note: If provided Authority contains an ID that does not exist
     * in the table, the ID will be ignored.
     * </pre> {@inheritDoc}
     *
     * @throws Exception
     */
    public void saveAuthority(Authority authority) throws Exception {
        if (existsInTable(authority)) {
            doUpdate(authority);
        } else {
            doInsert(authority);
        }
    }

    private boolean existsInTable(Authority authority) {
        Authority existingAuthority = null;
        try {
            existingAuthority =
                    findAuthorityByUsername(authority.getUsername());
        } catch (Exception e) {
        }
        return existingAuthority != null;
    }

    @SuppressWarnings("unchecked")
    private void doUpdate(Authority authority) throws Exception {
        StringBuilder sb = new StringBuilder("UPDATE " + tablename + " SET ");
        int len = sb.length();
        if (authority.getUsername() != null) {
            sb.append(usernameCol + " = '" + authority.getUsername() + "', ");
        }
        if (authority.getAuthority() != null) {
            sb.append(authorityCol + " = '" + authority.getAuthority() + "', ");
        }

        // Verify need for update.
        if (sb.length() == len) {
            throw new Exception("Nothing to update, all fields are null!");
        }

        sb.deleteCharAt(sb.lastIndexOf(", "));
        sb
                .append("WHERE " + usernameCol + " = '"
                        + authority.getUsername() + "'");

        log.debug("Executing update: '" + sb.toString() + "'");
        this.getSimpleJdbcTemplate().update(sb.toString(), new HashMap());
    }

    private void doInsert(Authority authority) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(usernameCol, authority.getUsername());
        params.put(authorityCol, authority.getAuthority());

        this.getSimpleJdbcTemplate().update(AUTHORITY_INSERT, params);
    }

    /**
     * <pre>
     * This method returns the Authority of the provided ID.
     * If no Authority is found, an exception is thrown.
     * </pre> {@inheritDoc}
     */
    public Authority findAuthorityByUsername(String username) throws Exception {
        List<Authority> authorities =
                this.getSimpleJdbcTemplate()
                        .query(AUTHORITY_SELECT_BY_USERNAME,
                               new ParameterizedRowMapper<Authority>() {

                                   public Authority mapRow(ResultSet rs,
                                                           int rowNum)
                                           throws SQLException {
                                       Authority authority = new Authority();
                                       authority.setUsername(rs
                                               .getString(usernameCol));
                                       authority.setAuthority(rs
                                               .getString(authorityCol));
                                       return authority;
                                   }
                               },
                               username);
        if (authorities.size() == 0) {
            throw new Exception("Authority not found with username: '"
                    + username + "'");
        }
        return authorities.get(0);
    }

    /**
     * <pre>
     * This method returns all IDs in the table.
     * If no results are found, an exception is thrown.
     * </pre> {@inheritDoc}
     */
    public List<String> getAuthorityUsernames() throws Exception {
        List<String> usernames =
                this.getSimpleJdbcTemplate()
                        .query(USERNAMES_SELECT,
                               new ParameterizedRowMapper<String>() {

                                   public String mapRow(ResultSet rs, int rowNum)
                                           throws SQLException {
                                       return new String(rs
                                               .getString(usernameCol));
                                   }
                               });
        if (usernames.size() == 0) {
            throw new Exception("Table is empty: '" + tablename + "'");
        }
        return usernames;
    }

    public static TableSpec getTableSpec() {
        TableSpec ts = new TableSpec();
        ts.setTableName(tablename);
        ts.setPrimaryKey(usernameCol);
        ts.setDdl(ddl);
        return ts;
    }
}
