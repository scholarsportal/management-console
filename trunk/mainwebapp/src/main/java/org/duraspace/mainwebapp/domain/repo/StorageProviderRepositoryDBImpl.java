
package org.duraspace.mainwebapp.domain.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.duraspace.common.util.TableSpec;
import org.duraspace.mainwebapp.domain.model.StorageProvider;
import org.duraspace.storage.domain.StorageProviderType;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * This class providers an ORM to the READ-ONLY StorageProvider table.
 *
 * @author Andrew Woods
 */
public class StorageProviderRepositoryDBImpl
        extends SimpleJdbcDaoSupport
        implements StorageProviderRepository {

    protected final Logger log = Logger.getLogger(getClass());

    private final static String tablename = "StorageProvider";

    private final static String idCol = "id";

    private final static String providerNameCol = "providerName";

    private final static String providerTypeCol = "providerType";

    private final static String urlCol = "url";

    private final String STORAGE_PROVIDER_INSERT =
            "INSERT INTO " + tablename + " (" + providerNameCol + ", "
                    + providerTypeCol + ", " + urlCol + ") " + "VALUES (:"
                    + providerNameCol + ", :" + providerTypeCol + ", :"
                    + urlCol + ")";

    private final String STORAGE_PROVIDER_SELECT_BY_ID =
            "SELECT " + idCol + "," + providerNameCol + "," + providerTypeCol
                    + "," + urlCol + " FROM  " + tablename + " WHERE id = ?";

    private final String ID_SELECT = "SELECT id FROM " + tablename;

    private final String ID_SELECT_FOR_SINGLE_STORAGE_PROVIDER =
            ID_SELECT + " WHERE ";

    private final String ID_SELECT_BY_PROVIDER_TYPE =
            ID_SELECT + " WHERE " + providerTypeCol + " = ?";

    // Leaving DDL hard-coded for legibility.
    private final static String ddl =
            "CREATE TABLE StorageProvider ("
                    + "id INT GENERATED ALWAYS AS IDENTITY,"
                    + "providerName VARCHAR(64) NOT NULL,"
                    + "providerType VARCHAR(32) NOT NULL UNIQUE,"
                    + "url VARCHAR(128) NOT NULL," + "PRIMARY KEY (id))";

    /**
     * {@inheritDoc}
     */
    public StorageProvider findStorageProviderById(int id) throws Exception {
        List<StorageProvider> providers =
                this.getSimpleJdbcTemplate()
                        .query(STORAGE_PROVIDER_SELECT_BY_ID,
                               new StorageProviderRowMapper(),
                               id);
        if (providers.size() == 0) {
            throw new Exception("StorageProvider not found with id: '" + id
                    + "'");
        }
        return providers.get(0);
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> getStorageProviderIds() throws Exception {
        List<Integer> ids =
                this.getSimpleJdbcTemplate().query(ID_SELECT,
                                                   new IntegerRowMapper());
        if (ids.size() == 0) {
            throw new Exception("Table is empty: '" + tablename + "'");
        }
        return ids;
    }

    public int saveStorageProvider(StorageProvider storageProvider)
            throws Exception {
        if (existsInTable(storageProvider)) {
            return doUpdate(storageProvider);
        } else {
            return doInsert(storageProvider);
        }
    }

    private boolean existsInTable(StorageProvider provider) {
        StorageProvider existingProvider = null;
        try {
            existingProvider = findStorageProviderById(findIdFor(provider));
        } catch (Exception e) {
        }
        return existingProvider != null;
    }

    @SuppressWarnings("unchecked")
    private int doUpdate(StorageProvider provider) throws Exception {
        StringBuilder sb = new StringBuilder("UPDATE " + tablename + " SET ");
        int len = sb.length();
        if (provider.getProviderName() != null) {
            sb.append(providerNameCol + " = '" + provider.getProviderName()
                    + "', ");
        }
        if (provider.getProviderType() != null) {
            sb.append(providerTypeCol + " = '"
                    + provider.getProviderType().toString() + "', ");
        }
        if (provider.getUrl() != null) {
            sb.append(urlCol + " = '" + provider.getUrl() + "', ");
        }

        // Verify need for update.
        if (sb.length() == len) {
            throw new Exception("Nothing to update, all fields are null!");
        }

        sb.deleteCharAt(sb.lastIndexOf(", "));
        sb.append("WHERE " + idCol + " = " + provider.getId() + "");

        log.debug("Executing update: '" + sb.toString() + "'");
        this.getSimpleJdbcTemplate().update(sb.toString(), new HashMap());

        return findIdFor(provider);
    }

    private int doInsert(StorageProvider provider) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put(providerNameCol, provider.getProviderName());
        params.put(providerTypeCol, provider.getProviderType().toString());
        params.put(urlCol, provider.getUrl());

        this.getSimpleJdbcTemplate().update(STORAGE_PROVIDER_INSERT, params);

        return findIdFor(provider);
    }

    public int findStorageProviderIdByProviderType(StorageProviderType providerType)
            throws Exception {
        List<Integer> ids =
                this.getSimpleJdbcTemplate().query(ID_SELECT_BY_PROVIDER_TYPE,
                                                   new IntegerRowMapper(),
                                                   providerType.toString());
        if (ids.size() == 0) {
            throw new Exception("Table is empty: '" + tablename + "'");
        }
        if (ids.size() != 1) {
            throw new Exception(tablename
                    + " contains more than one entry for providerType: "
                    + providerType.toString());
        }

        return ids.get(0);
    }

    @SuppressWarnings("unchecked")
    private int findIdFor(StorageProvider provider) throws Exception {
        Integer id = null;
        if (provider.hasId()) {
            id = provider.getId();
        } else {
            StringBuilder sb =
                    new StringBuilder(ID_SELECT_FOR_SINGLE_STORAGE_PROVIDER
                            + " (");
            int len = sb.length();
            if (provider.getProviderName() != null) {
                sb.append(providerNameCol + " = '" + provider.getProviderName()
                        + "' AND ");
            }
            if (provider.getProviderType() != null) {
                sb.append(providerTypeCol + " = '"
                        + provider.getProviderType().toString() + "' AND ");
            }
            if (provider.getUrl() != null) {
                sb.append(urlCol + " = '" + provider.getUrl() + "' AND ");
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
            throw new Exception("ID not found for : '" + provider + "'");
        }
        return id;
    }

    /**
     * This class provides a mapping of ResultSet entries to their
     * StorageProvider representation.
     */
    private class StorageProviderRowMapper
            implements ParameterizedRowMapper<StorageProvider> {

        public StorageProvider mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            StorageProvider provider = new StorageProvider();
            provider.setId(rs.getInt(idCol));
            provider.setProviderName(rs.getString(providerNameCol));
            provider.setProviderType(rs.getString(providerTypeCol));
            provider.setUrl(rs.getString(urlCol));
            return provider;
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
