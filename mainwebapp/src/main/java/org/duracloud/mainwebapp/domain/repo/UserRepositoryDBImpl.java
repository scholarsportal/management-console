/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.duracloud.common.util.TableSpec;
import org.duracloud.mainwebapp.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class UserRepositoryDBImpl
        extends SimpleJdbcDaoSupport
        implements UserRepository {

    protected final Logger log = LoggerFactory.getLogger(UserRepositoryDBImpl.class);

    private final static String tablename = "Customer";

    private final static String idCol = "id";

    private final static String lastnameCol = "lastname";

    private final static String firstnameCol = "firstname";

    private final static String emailCol = "email";

    private final static String phoneWorkCol = "phoneWork";

    private final static String phoneOtherCol = "phoneOther";

    private final static String addrShippingIdCol = "addrShipping_id";

    private final static String credentialIdCol = "credential_id";

    private final static String duraAcctIdCol = "duraAcct_id";

    private final String USER_INSERT =
            "INSERT INTO " + tablename + " (" + lastnameCol + ","
                    + firstnameCol + "," + emailCol + "," + phoneWorkCol + ","
                    + phoneOtherCol + "," + addrShippingIdCol + ","
                    + credentialIdCol + "," + duraAcctIdCol + ") "
                    + "VALUES (:" + lastnameCol + ",:" + firstnameCol + ",:"
                    + emailCol + ",:" + phoneWorkCol + ",:" + phoneOtherCol
                    + ",:" + addrShippingIdCol + ",:" + credentialIdCol + ",:"
                    + duraAcctIdCol + ")";

    private final String USER_SELECT =
            "SELECT " + idCol + "," + lastnameCol + "," + firstnameCol + ","
                    + emailCol + "," + phoneWorkCol + "," + phoneOtherCol + ","
                    + addrShippingIdCol + "," + credentialIdCol + ","
                    + duraAcctIdCol + " FROM " + tablename;

    private final String USER_SELECT_BY_ID =
            USER_SELECT + " WHERE " + idCol + " = ?";

    private final String USER_SELECT_BY_CRED_ID =
            USER_SELECT + " WHERE " + credentialIdCol + " = ?";

    private final String USER_SELECT_BY_DURA_ACCT_ID =
            USER_SELECT + " WHERE " + duraAcctIdCol + " = ?";

    private final String ID_SELECT = "SELECT " + idCol + " FROM " + tablename;

    private final String ID_SELECT_FOR_SINGLE_USER = ID_SELECT + " WHERE ";

    // Leaving DDL hard-coded for legibility.
    private final static String ddl =
            "CREATE TABLE Customer ("
                    + "id INT GENERATED ALWAYS AS IDENTITY,"
                    + "lastname VARCHAR(64) NOT NULL,"
                    + "firstname VARCHAR(64) NOT NULL,"
                    + "email VARCHAR(64) NOT NULL,"
                    + "phoneWork VARCHAR(16),"
                    + "phoneOther VARCHAR(16),"
                    + "addrShipping_id INT,"
                    + "credential_id INT,"
                    + "duraAcct_id INT,"
                    + "FOREIGN KEY (addrShipping_id) REFERENCES Address (id),"
                    + "FOREIGN KEY (credential_id) REFERENCES Credential (id),"
                    + "FOREIGN KEY (duraAcct_id) REFERENCES DuraCloudAcct (id),"
                    + "PRIMARY KEY (id))";

    /**
     * {@inheritDoc}
     */
    public int saveUser(User user) throws Exception {
        if (existsInTable(user)) {
            return doUpdate(user);
        } else {
            return doInsert(user);
        }
    }

    private boolean existsInTable(User user) {
        User existingUser = null;
        try {
            existingUser = findUserById(findIdFor(user));
        } catch (Exception e) {
        }
        return existingUser != null;
    }

    @SuppressWarnings("unchecked")
    private int doUpdate(User user) throws Exception {
        StringBuilder sb = new StringBuilder("UPDATE " + tablename + " SET ");
        int len = sb.length();
        if (user.getLastname() != null) {
            sb.append(lastnameCol + " = '" + user.getLastname() + "', ");
        }
        if (user.getFirstname() != null) {
            sb.append(firstnameCol + " = '" + user.getFirstname() + "', ");
        }
        if (user.getEmail() != null) {
            sb.append(emailCol + " = '" + user.getEmail() + "', ");
        }
        if (user.getPhoneWork() != null) {
            sb.append(phoneWorkCol + " = '" + user.getPhoneWork() + "', ");
        }
        if (user.getPhoneOther() != null) {
            sb.append(phoneOtherCol + " = '" + user.getPhoneOther() + "', ");
        }
        if (user.hasAddrShippingId()) {
            sb.append(addrShippingIdCol + " = " + user.getAddrShippingId()
                    + ", ");
        }
        if (user.hasCredentialId()) {
            sb.append(credentialIdCol + " = " + user.getCredentialId() + ", ");
        }
        if (user.hasDuraAcctId()) {
            sb.append(duraAcctIdCol + " = " + user.getDuraAcctId() + ", ");
        }

        // Verify need for update.
        if (sb.length() == len) {
            throw new Exception("Nothing to update, all fields are null!");
        }

        sb.deleteCharAt(sb.lastIndexOf(", "));
        sb.append("WHERE " + idCol + " = " + user.getId() + "");

        log.debug("Executing update: '" + sb.toString() + "'");
        this.getSimpleJdbcTemplate().update(sb.toString(), new HashMap());

        return findIdFor(user);
    }

    private int doInsert(User user) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(lastnameCol, user.getLastname());
        params.put(firstnameCol, user.getFirstname());
        params.put(emailCol, user.getEmail());
        params.put(phoneWorkCol, user.getPhoneWork());
        params.put(phoneOtherCol, user.getPhoneOther());
        if (user.hasAddrShippingId()) {
            params.put(addrShippingIdCol, user.getAddrShippingId());
        } else {
            params.put(addrShippingIdCol, null);
        }
        if (user.hasCredentialId()) {
            params.put(credentialIdCol, user.getCredentialId());
        } else {
            params.put(credentialIdCol, null);
        }
        if (user.hasDuraAcctId()) {
            params.put(duraAcctIdCol, user.getDuraAcctId());
        } else {
            params.put(duraAcctIdCol, null);
        }

        this.getSimpleJdbcTemplate().update(USER_INSERT, params);

        return findIdFor(user);
    }

    /**
     * {@inheritDoc}
     */
    public User findUserById(int id) throws Exception {
        List<User> users =
                this.getSimpleJdbcTemplate().query(USER_SELECT_BY_ID,
                                                   new UserRowMapper(),
                                                   id);
        if (users.size() == 0) {
            throw new Exception(tablename + " not found with id: '" + id + "'");
        }
        if (users.size() != 1) {
            throw new Exception(tablename + " more than 1 user with id: '" + id
                    + "'");
        }

        return users.get(0);
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> getUserIds() throws Exception {
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

    // TODO:awoods :try to remove this method
    public User findUserByDuraCredId(int id) throws Exception {
        List<User> users =
                this.getSimpleJdbcTemplate().query(USER_SELECT_BY_CRED_ID,
                                                   new UserRowMapper(),
                                                   id);
        if (users.size() == 0) {
            throw new Exception(tablename + " not found with id: '" + id + "'");
        }
        if (users.size() != 1) {
            throw new Exception(tablename + " more than 1 user with id: '" + id
                    + "'");
        }
        return users.get(0);
    }

    public List<User> findUsersByDuraAcctId(int duraAcctId) throws Exception {
        List<User> users =
                this.getSimpleJdbcTemplate().query(USER_SELECT_BY_DURA_ACCT_ID,
                                                   new UserRowMapper(),
                                                   duraAcctId);
        if (users.size() == 0) {
            throw new Exception(tablename + " not found with duraAcctId: '"
                    + duraAcctId + "'");
        }
        return users;

    }

    public static TableSpec getTableSpec() {
        TableSpec ts = new TableSpec();
        ts.setTableName(tablename);
        ts.setPrimaryKey(idCol);
        ts.setDdl(ddl);
        return ts;
    }

    @SuppressWarnings("unchecked")
    private int findIdFor(User user) throws Exception {
        Integer id = null;
        if (user.hasId()) {
            id = user.getId();
        } else {
            StringBuilder sb =
                    new StringBuilder(ID_SELECT_FOR_SINGLE_USER + " (");
            int len = sb.length();
            if (user.getLastname() != null) {
                sb.append(lastnameCol + " = '" + user.getLastname() + "' AND ");
            }
            if (user.getFirstname() != null) {
                sb.append(firstnameCol + " = '" + user.getFirstname()
                        + "' AND ");
            }
            if (user.getEmail() != null) {
                sb.append(emailCol + " = '" + user.getEmail() + "' AND ");
            }
            if (user.getPhoneWork() != null) {
                sb.append(phoneWorkCol + " = '" + user.getPhoneWork()
                        + "' AND ");
            }
            if (user.getPhoneOther() != null) {
                sb.append(phoneOtherCol + " = '" + user.getPhoneOther()
                        + "' AND ");
            }
            if (user.hasAddrShippingId()) {
                sb.append(addrShippingIdCol + " = " + user.getAddrShippingId()
                        + "  AND ");
            }
            if (user.hasCredentialId()) {
                sb.append(credentialIdCol + " = " + user.getCredentialId()
                        + " AND ");
            }
            if (user.hasDuraAcctId()) {
                sb.append(duraAcctIdCol + " = " + user.getDuraAcctId()
                        + " AND ");
            }

            // Verify need for update.
            if (sb.length() == len) {
                throw new Exception("Unable to find ID, all fields are null!");
            }

            sb.delete(sb.lastIndexOf("AND "), sb.length());
            sb.append(")");

            log.debug("Executing query: '" + sb.toString() + "'");
            id =
                    this.getSimpleJdbcTemplate().queryForInt(sb.toString(),
                                                             new HashMap());
        }

        if (id == null) {
            throw new Exception("ID not found for : '" + user + "'");
        }
        return id;
    }

    /**
     * This class provides a mapping of ResultSet items to their User
     * representation
     *
     * @author Andrew Woods
     */
    private class UserRowMapper
            implements ParameterizedRowMapper<User> {

        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt(idCol));
            user.setLastname(rs.getString(lastnameCol));
            user.setFirstname(rs.getString(firstnameCol));
            user.setEmail(rs.getString(emailCol));
            user.setPhoneWork(rs.getString(phoneWorkCol));
            user.setPhoneOther(rs.getString(phoneOtherCol));
            user.setAddrShippingId(rs.getInt(addrShippingIdCol));
            user.setCredentialId(rs.getInt(credentialIdCol));
            user.setDuraAcctId(rs.getInt(duraAcctIdCol));
            return user;
        }

    }
}
