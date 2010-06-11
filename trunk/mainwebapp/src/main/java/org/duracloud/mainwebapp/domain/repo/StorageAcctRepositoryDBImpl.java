package org.duracloud.mainwebapp.domain.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.duracloud.common.util.TableSpec;
import org.duracloud.mainwebapp.domain.model.StorageAcct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class StorageAcctRepositoryDBImpl
        extends SimpleJdbcDaoSupport
        implements StorageAcctRepository {

    protected final Logger log = LoggerFactory.getLogger(StorageAcctRepositoryDBImpl.class);

    private final static String tablename = "StorageAcct";

    private final static String idCol = "id";

    private final static String isPrimaryCol = "isPrimary";

    private final static String namespaceCol = "namespace";

    private final static String storageProviderTypeCol = "storageProviderType";

    private final static String storageProviderIdCol = "storageProvider_id";

    private final static String credentialIdCol = "credential_id";

    private final static String duraAcctIdCol = "duraAcct_id";

    private final String STORAGE_ACCT_INSERT =
            "INSERT INTO " + tablename + " (" + isPrimaryCol + ","
                    + namespaceCol + "," + storageProviderTypeCol + ","
                    + storageProviderIdCol + "," + credentialIdCol + ","
                    + duraAcctIdCol + ") " + "VALUES (:" + isPrimaryCol + ",:"
                    + namespaceCol + ",:" + storageProviderTypeCol + ",:"
                    + storageProviderIdCol + ",:" + credentialIdCol + ",:"
                    + duraAcctIdCol + ")";

    private final String STORAGE_ACCT_SELECT =
            "SELECT " + idCol + "," + isPrimaryCol + "," + namespaceCol + ","
                    + storageProviderTypeCol + "," + storageProviderIdCol + ","
                    + credentialIdCol + "," + duraAcctIdCol + " FROM "
                    + tablename;

    private final String STORAGE_ACCT_SELECT_BY_ID =
            STORAGE_ACCT_SELECT + " WHERE " + idCol + " = ?";

    private final String STORAGE_ACCT_SELECT_BY_DURAACCT_ID =
            STORAGE_ACCT_SELECT + " WHERE " + duraAcctIdCol + " = ?";

    private final String ID_SELECT = "SELECT " + idCol + " FROM " + tablename;

    private final String ID_SELECT_FOR_SINGLE_STORAGE_ACCT =
            ID_SELECT + " WHERE ";

    private final String ID_SELECT_BY_NAMESPACE =
            ID_SELECT + " WHERE " + namespaceCol + " = ?";

    // Leaving DDL hard-coded for legibility.
    private final static String ddl =
            "CREATE TABLE StorageAcct ("
                    + "id INT GENERATED ALWAYS AS IDENTITY,"
                    + "isPrimary INTEGER NOT NULL DEFAULT 1,"
                    + "namespace VARCHAR(64) NOT NULL UNIQUE,"
                    + "storageProviderType VARCHAR(32) NOT NULL,"
                    + "storageProvider_id INT,"
                    + "credential_id INT,"
                    + "duraAcct_id INT,"
                    + "FOREIGN KEY (storageProvider_id) REFERENCES StorageProvider (id),"
                    + "FOREIGN KEY (credential_id) REFERENCES Credential (id),"
                    + "FOREIGN KEY (duraAcct_id) REFERENCES DuraCloudAcct (id),"
                    + "PRIMARY KEY (id))";

    /**
     * {@inheritDoc}
     */
    public int saveStorageAcct(StorageAcct user) throws Exception {
        if (existsInTable(user)) {
            return doUpdate(user);
        } else {
            return doInsert(user);
        }
    }

    private boolean existsInTable(StorageAcct acct) {
        StorageAcct existingAcct = null;
        try {
            existingAcct = findStorageAcctById(findIdFor(acct));
        } catch (Exception e) {
        }
        return existingAcct != null;
    }

    @SuppressWarnings("unchecked")
    private int doUpdate(StorageAcct acct) throws Exception {
        StringBuilder sb = new StringBuilder("UPDATE " + tablename + " SET ");
        int len = sb.length();

        sb.append(isPrimaryCol + " = " + acct.getIsPrimary() + ", ");
        if (acct.getNamespace() != null) {
            sb.append(namespaceCol + " = '" + acct.getNamespace() + "', ");
        }

        if (acct.getStorageProviderType() != null) {
            sb.append(storageProviderTypeCol + " = '"
                    + acct.getStorageProviderType() + "', ");
        }
        if (acct.hasStorageProviderId()) {
            sb.append(storageProviderIdCol + " = "
                    + acct.getStorageProviderId() + ", ");
        }
        if (acct.hasStorageCredentialId()) {
            sb.append(credentialIdCol + " = " + acct.getStorageCredentialId()
                    + ", ");
        }
        if (acct.hasDuraAcctId()) {
            sb.append(duraAcctIdCol + " = " + acct.getDuraAcctId() + ", ");
        }

        // Verify need for update.
        if (sb.length() == len) {
            throw new Exception("Nothing to update, all fields are null!");
        }

        sb.deleteCharAt(sb.lastIndexOf(", "));
        sb.append("WHERE " + idCol + " = " + acct.getId() + "");

        log.debug("Executing update: '" + sb.toString() + "'");
        this.getSimpleJdbcTemplate().update(sb.toString(), new HashMap());

        return findIdFor(acct);
    }

    private int doInsert(StorageAcct acct) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(isPrimaryCol, acct.getIsPrimary());
        params.put(namespaceCol, acct.getNamespace());
        params.put(storageProviderTypeCol, acct.getStorageProviderType()
                .toString());
        if (acct.hasStorageProviderId()) {
            params.put(storageProviderIdCol, acct.getStorageProviderId());
        } else {
            params.put(storageProviderIdCol, null);
        }
        if (acct.hasStorageCredentialId()) {
            params.put(credentialIdCol, acct.getStorageCredentialId());
        } else {
            params.put(credentialIdCol, null);
        }
        if (acct.hasDuraAcctId()) {
            params.put(duraAcctIdCol, acct.getDuraAcctId());
        } else {
            params.put(duraAcctIdCol, null);
        }

        this.getSimpleJdbcTemplate().update(STORAGE_ACCT_INSERT, params);

        return findIdFor(acct);
    }

    /**
     * {@inheritDoc}
     */
    public StorageAcct findStorageAcctById(int id) throws Exception {
        List<StorageAcct> accts =
                this.findStorageAccts(STORAGE_ACCT_SELECT_BY_ID, id);

        if (accts.size() == 0) {
            throw new Exception(tablename + " not found with id: '" + id + "'");
        }
        if (accts.size() != 1) {
            throw new Exception(tablename
                    + " more than 1 StorageAcct with id: '" + id + "'");
        }

        return accts.get(0);
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> getStorageAcctIds() throws Exception {
        List<Integer> ids =
                this.getSimpleJdbcTemplate().query(ID_SELECT,
                                                   new IntegerRowMapper());
        if (ids.size() == 0) {
            throw new Exception("Table is empty: '" + tablename + "'");
        }
        return ids;
    }

    /**
     * {@inheritDoc}
     */
    public List<StorageAcct> findStorageAcctsByDuraAcctId(int id)
            throws Exception {
        List<StorageAcct> accts =
                findStorageAccts(this.STORAGE_ACCT_SELECT_BY_DURAACCT_ID, id);

        if (accts.size() == 0) {
            throw new Exception(tablename + " not found with id: '" + id + "'");
        }

        return accts;
    }

    private List<StorageAcct> findStorageAccts(String query, Object... params) {
        List<StorageAcct> accts =
                this.getSimpleJdbcTemplate().query(query,
                                                   new StorageAcctRowMapper(),
                                                   params);
        return accts;
    }

    public boolean isStorageNamespaceTaken(String storageAcctNamespace) {
        List<Integer> ids =
                this.getSimpleJdbcTemplate().query(ID_SELECT_BY_NAMESPACE,
                                                   new IntegerRowMapper(),
                                                   storageAcctNamespace);

        return ids.size() != 0;
    }

    @SuppressWarnings("unchecked")
    private int findIdFor(StorageAcct acct) throws Exception {
        Integer id = null;
        if (acct.hasId()) {
            id = acct.getId();
        } else {
            StringBuilder sb =
                    new StringBuilder(ID_SELECT_FOR_SINGLE_STORAGE_ACCT + " (");
            int len = sb.length();
            //        if (acct.getIsPrimary() != null)
            {
                sb.append(isPrimaryCol + " = " + acct.getIsPrimary() + " AND ");
            }
            if (acct.getNamespace() != null) {
                sb.append(namespaceCol + " = '" + acct.getNamespace()
                        + "' AND ");
            }

            if (acct.getStorageProviderType() != null) {
                sb.append(storageProviderTypeCol + " = '"
                        + acct.getStorageProviderType() + "' AND ");
            }
            if (acct.hasStorageProviderId()) {
                sb.append(storageProviderIdCol + " = "
                        + acct.getStorageProviderId() + " AND ");
            }
            if (acct.hasStorageCredentialId()) {
                sb.append(credentialIdCol + " = "
                        + acct.getStorageCredentialId() + " AND ");
            }
            if (acct.hasDuraAcctId()) {
                sb.append(duraAcctIdCol + " = " + acct.getDuraAcctId()
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
            throw new Exception("ID not found for : '" + acct + "'");
        }
        return id;
    }

    /**
     * This class provides a mapping of ResultSet entries to their StorageAcct
     * representation.
     */
    private class StorageAcctRowMapper
            implements ParameterizedRowMapper<StorageAcct> {

        public StorageAcct mapRow(ResultSet rs, int rowNum) throws SQLException {
            StorageAcct acct = new StorageAcct();
            acct.setId(rs.getInt(idCol));
            acct.setPrimary(rs.getInt(isPrimaryCol));
            acct.setNamespace(rs.getString(namespaceCol));
            acct.setStorageProviderType(rs.getString(storageProviderTypeCol));
            acct.setStorageProviderId(rs.getInt(storageProviderIdCol));
            acct.setStorageCredentialId(rs.getInt(credentialIdCol));
            acct.setDuraAcctId(rs.getInt(duraAcctIdCol));
            return acct;
        }
    }

    /**
     * This class provides a mapping of ResultSet entries to their Integer
     * representation.
     */
    private class IntegerRowMapper
            implements ParameterizedRowMapper<Integer> {

        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Integer(rs.getString(idCol));
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
