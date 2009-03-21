
package org.duraspace.mainwebapp.domain.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.duraspace.common.util.TableSpec;
import org.duraspace.mainwebapp.domain.model.ComputeProvider;
import org.duraspace.serviceprovider.domain.ComputeProviderType;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * This class providers an ORM to the READ-ONLY ComputeProvider table.
 *
 * @author Andrew Woods
 */
public class ComputeProviderRepositoryDBImpl
        extends SimpleJdbcDaoSupport
        implements ComputeProviderRepository {

    protected final Logger log = Logger.getLogger(getClass());

    private final static String tablename = "ComputeProvider";

    private final static String idCol = "id";

    private final static String providerNameCol = "providerName";

    private final static String providerTypeCol = "providerType";

    private final static String urlCol = "url";

    private final String COMPUTE_PROVIDER_INSERT =
            "INSERT INTO " + tablename + " (" + providerNameCol + ", "
                    + providerTypeCol + ", " + urlCol + ") " + "VALUES (:"
                    + providerNameCol + ", :" + providerTypeCol + ", :"
                    + urlCol + ")";

    private final String COMPUTE_PROVIDER_SELECT_BY_ID =
            "SELECT " + idCol + "," + providerNameCol + "," + providerTypeCol
                    + "," + urlCol + " FROM  " + tablename + " WHERE id = ?";

    private final String ID_SELECT = "SELECT id FROM " + tablename;

    private final String ID_SELECT_FOR_SINGLE_COMPUTE_PROVIDER =
            ID_SELECT + " WHERE ";

    private final String ID_SELECT_BY_PROVIDER_TYPE =
            ID_SELECT + " WHERE " + providerTypeCol + " = ?";

    // Leaving DDL hard-coded for legibility.
    private final static String ddl =
            "CREATE TABLE ComputeProvider ("
                    + "id INT GENERATED ALWAYS AS IDENTITY,"
                    + "providerName VARCHAR(64) NOT NULL,"
                    + "providerType VARCHAR(32) NOT NULL UNIQUE,"
                    + "url VARCHAR(128) NOT NULL," + "PRIMARY KEY (id))";

    /**
     * {@inheritDoc}
     */
    public ComputeProvider findComputeProviderById(int id) throws Exception {
        List<ComputeProvider> providers =
                this.getSimpleJdbcTemplate()
                        .query(COMPUTE_PROVIDER_SELECT_BY_ID,
                               new ParameterizedRowMapper<ComputeProvider>() {

                                   public ComputeProvider mapRow(ResultSet rs,
                                                                 int rowNum)
                                           throws SQLException {
                                       ComputeProvider provider =
                                               new ComputeProvider();
                                       provider.setId(rs.getInt(idCol));
                                       provider.setProviderName(rs
                                               .getString(providerNameCol));
                                       provider.setProviderType(rs
                                               .getString(providerTypeCol));
                                       provider.setUrl(rs.getString(urlCol));
                                       return provider;
                                   }
                               },
                               id);
        if (providers.size() == 0) {
            throw new Exception("ComputeProvider not found with id: '" + id
                    + "'");
        }
        return providers.get(0);
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> getComputeProviderIds() throws Exception {
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

    public int saveComputeProvider(ComputeProvider computeProvider)
            throws Exception {
        if (existsInTable(computeProvider)) {
            return doUpdate(computeProvider);
        } else {
            return doInsert(computeProvider);
        }
    }

    private boolean existsInTable(ComputeProvider provider) {
        ComputeProvider existingProvider = null;
        try {
            existingProvider = findComputeProviderById(findIdFor(provider));
        } catch (Exception e) {
        }
        return existingProvider != null;
    }

    @SuppressWarnings("unchecked")
    private int doUpdate(ComputeProvider provider) throws Exception {
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

    private int doInsert(ComputeProvider provider) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put(providerNameCol, provider.getProviderName());
        params.put(providerTypeCol, provider.getProviderType().toString());
        params.put(urlCol, provider.getUrl());

        this.getSimpleJdbcTemplate().update(COMPUTE_PROVIDER_INSERT, params);

        return findIdFor(provider);
    }

    public int findComputeProviderIdByProviderType(ComputeProviderType providerType)
            throws Exception {
        List<Integer> ids =
                this.getSimpleJdbcTemplate()
                        .query(ID_SELECT_BY_PROVIDER_TYPE,
                               new ParameterizedRowMapper<Integer>() {

                                   public Integer mapRow(ResultSet rs,
                                                         int rowNum)
                                           throws SQLException {
                                       return new Integer(rs.getString(idCol));
                                   }
                               },
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
    private int findIdFor(ComputeProvider provider) throws Exception {
        Integer id = null;
        if (provider.hasId()) {
            id = provider.getId();
        } else {
            StringBuilder sb =
                    new StringBuilder(ID_SELECT_FOR_SINGLE_COMPUTE_PROVIDER
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

    public static TableSpec getTableSpec() {
        TableSpec ts = new TableSpec();
        ts.setTableName(tablename);
        ts.setPrimaryKey(idCol);
        ts.setDdl(ddl);
        return ts;
    }

}
