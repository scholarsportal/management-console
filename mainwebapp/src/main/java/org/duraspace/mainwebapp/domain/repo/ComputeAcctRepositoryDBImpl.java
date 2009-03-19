
package org.duraspace.mainwebapp.domain.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.duraspace.common.util.TableSpec;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class ComputeAcctRepositoryDBImpl
        extends SimpleJdbcDaoSupport
        implements ComputeAcctRepository {

    protected final Logger log = Logger.getLogger(getClass());

    private final static String tablename = "ComputeAcct";

    private final static String idCol = "id";

    private final static String namespaceCol = "namespace";

    private final static String instanceIdCol = "instanceId";

    private final static String computePropsCol = "computeProps";

    private final static String computeProviderTypeCol = "computeProviderType";

    private final static String computeProviderIdCol = "computeProvider_id";

    private final static String credentialIdCol = "credential_id";

    private final static String duraAcctIdCol = "duraAcct_id";

    private final String COMPUTE_ACCT_INSERT =
            "INSERT INTO " + tablename + " (" + namespaceCol + ","
                    + instanceIdCol + "," + computePropsCol + ","
                    + computeProviderTypeCol + "," + computeProviderIdCol + ","
                    + credentialIdCol + "," + duraAcctIdCol + ") "
                    + "VALUES (:" + namespaceCol + ",:" + instanceIdCol + ",:"
                    + computePropsCol + ",:" + computeProviderTypeCol + ",:"
                    + computeProviderIdCol + ",:" + credentialIdCol + ",:"
                    + duraAcctIdCol + ")";

    private final String COMPUTE_ACCT_SELECT =
            "SELECT " + idCol + "," + namespaceCol + "," + instanceIdCol + ","
                    + computePropsCol + "," + computeProviderTypeCol + ","
                    + computeProviderIdCol + "," + credentialIdCol + ","
                    + duraAcctIdCol + " FROM " + tablename;

    private final String COMPUTE_ACCT_SELECT_BY_ID =
            COMPUTE_ACCT_SELECT + " WHERE " + idCol + " = ?";

    private final String COMPUTE_ACCT_SELECT_BY_DURAACCT_ID =
            COMPUTE_ACCT_SELECT + " WHERE " + duraAcctIdCol + " = ?";

    private final String ID_SELECT = "SELECT " + idCol + " FROM " + tablename;

    private final String ID_SELECT_FOR_SINGLE_COMPUTE_ACCT =
            ID_SELECT + " WHERE ";

    // Leaving DDL hard-coded for legibility.
    private final static String ddl =
            "CREATE TABLE ComputeAcct ("
                    + "id INT GENERATED ALWAYS AS IDENTITY,"
                    + "namespace VARCHAR(64) NOT NULL UNIQUE,"
                    + "instanceId VARCHAR(64),"
                    + "computeProps VARCHAR(1024),"
                    + "computeProviderType VARCHAR(32) NOT NULL,"
                    + "computeProvider_id INT,"
                    + "credential_id INT,"
                    + "duraAcct_id INT,"
                    + "FOREIGN KEY (computeProvider_id) REFERENCES ComputeProvider (id),"
                    + "FOREIGN KEY (credential_id) REFERENCES Credential (id),"
                    + "FOREIGN KEY (duraAcct_id) REFERENCES DuraSpaceAcct (id),"
                    + "PRIMARY KEY (id))";

    /**
     * {@inheritDoc}
     */
    public int saveComputeAcct(ComputeAcct acct) throws Exception {
        if (existsInTable(acct)) {
            return doUpdate(acct);
        } else {
            return doInsert(acct);
        }
    }

    private boolean existsInTable(ComputeAcct acct) {
        ComputeAcct existingAcct = null;
        try {
            existingAcct = findComputeAcctById(findIdFor(acct));
        } catch (Exception e) {
        }
        return existingAcct != null;
    }

    @SuppressWarnings("unchecked")
    private int doUpdate(ComputeAcct acct) throws Exception {
        StringBuilder sb = new StringBuilder("UPDATE " + tablename + " SET ");
        int len = sb.length();
        if (acct.getNamespace() != null) {
            sb.append(namespaceCol + " = '" + acct.getNamespace() + "', ");
        }
        if (acct.getInstanceId() != null) {
            sb.append(instanceIdCol + " = '" + acct.getInstanceId() + "', ");
        }
        if (acct.getXmlProps() != null) {
            sb.append(computePropsCol + " = '" + acct.getXmlProps() + "', ");
        }
        if (acct.getComputeProviderType() != null) {
            sb.append(computeProviderTypeCol + " = '"
                    + acct.getComputeProviderType() + "', ");
        }
        if (acct.hasComputeProviderId()) {
            sb.append(computeProviderIdCol + " = "
                    + acct.getComputeProviderId() + ", ");
        }
        if (acct.hasComputeCredentialId()) {
            sb.append(credentialIdCol + " = " + acct.getComputeCredentialId()
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

    private int doInsert(ComputeAcct acct) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(namespaceCol, acct.getNamespace());
        params.put(instanceIdCol, acct.getInstanceId());
        params.put(computePropsCol, acct.getXmlProps());
        params.put(computeProviderTypeCol, acct.getComputeProviderType()
                .toString());
        if (acct.hasComputeProviderId()) {
            params.put(computeProviderIdCol, acct.getComputeProviderId());
        } else {
            params.put(computeProviderIdCol, null);
        }
        if (acct.hasComputeCredentialId()) {
            params.put(credentialIdCol, acct.getComputeCredentialId());
        } else {
            params.put(credentialIdCol, null);
        }
        if (acct.hasDuraAcctId()) {
            params.put(duraAcctIdCol, acct.getDuraAcctId());
        } else {
            params.put(duraAcctIdCol, null);
        }

        this.getSimpleJdbcTemplate().update(COMPUTE_ACCT_INSERT, params);

        return findIdFor(acct);
    }

    /**
     * {@inheritDoc}
     */
    public ComputeAcct findComputeAcctById(int id) throws Exception {
        List<ComputeAcct> accts =
                this.getSimpleJdbcTemplate()
                        .query(COMPUTE_ACCT_SELECT_BY_ID,
                               new ParameterizedRowMapper<ComputeAcct>() {

                                   public ComputeAcct mapRow(ResultSet rs,
                                                             int rowNum)
                                           throws SQLException {
                                       ComputeAcct acct = new ComputeAcct();
                                       acct.setId(rs.getInt(idCol));
                                       acct.setNamespace(rs
                                               .getString(namespaceCol));
                                       acct.setInstanceId(rs
                                               .getString(instanceIdCol));
                                       acct.setXmlProps(rs
                                               .getString(computePropsCol));
                                       acct
                                               .setComputeProviderType(rs
                                                       .getString(computeProviderTypeCol));
                                       acct.setComputeProviderId(rs
                                               .getInt(computeProviderIdCol));
                                       acct.setComputeCredentialId(rs
                                               .getInt(credentialIdCol));
                                       acct.setDuraAcctId(rs
                                               .getInt(duraAcctIdCol));
                                       return acct;
                                   }
                               },
                               id);
        if (accts.size() == 0) {
            throw new Exception(tablename + " not found with id: '" + id + "'");
        }
        if (accts.size() != 1) {
            throw new Exception(tablename
                    + " more than 1 ComputeAcct with id: '" + id + "'");
        }

        return accts.get(0);
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> getComputeAcctIds() throws Exception {
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

    /**
     * {@inheritDoc}
     */
    public List<ComputeAcct> findComputeAcctsByDuraAcctId(int id) throws Exception {
        List<ComputeAcct> accts =
                this.getSimpleJdbcTemplate()
                        .query(COMPUTE_ACCT_SELECT_BY_DURAACCT_ID,
                               new ParameterizedRowMapper<ComputeAcct>() {

                                   public ComputeAcct mapRow(ResultSet rs,
                                                             int rowNum)
                                           throws SQLException {
                                       ComputeAcct acct = new ComputeAcct();
                                       acct.setId(rs.getInt(idCol));
                                       acct.setNamespace(rs
                                               .getString(namespaceCol));
                                       acct.setInstanceId(rs
                                               .getString(instanceIdCol));
                                       acct.setXmlProps(rs
                                               .getString(computePropsCol));
                                       acct
                                               .setComputeProviderType(rs
                                                       .getString(computeProviderTypeCol));
                                       acct.setComputeProviderId(rs
                                               .getInt(computeProviderIdCol));
                                       acct.setComputeCredentialId(rs
                                               .getInt(credentialIdCol));
                                       acct.setDuraAcctId(rs
                                               .getInt(duraAcctIdCol));
                                       return acct;
                                   }
                               },
                               id);
        if (accts.size() == 0) {
            throw new Exception(tablename + " not found with id: '" + id + "'");
        }
        return accts;
    }

    @SuppressWarnings("unchecked")
    private int findIdFor(ComputeAcct acct) throws Exception {
        Integer id = null;
        if (acct.hasId()) {
            id = acct.getId();
        } else {
            StringBuilder sb =
                    new StringBuilder(ID_SELECT_FOR_SINGLE_COMPUTE_ACCT + " (");
            int len = sb.length();
            if (acct.getNamespace() != null) {
                sb.append(namespaceCol + " = '" + acct.getNamespace()
                        + "' AND ");
            }
            if (acct.getInstanceId() != null) {
                sb.append(instanceIdCol + " = '" + acct.getInstanceId()
                        + "' AND ");
            }
            if (acct.getXmlProps() != null) {
                sb
                        .append(computePropsCol + " = '" + acct.getXmlProps()
                                + "' AND ");
            }
            if (acct.getComputeProviderType() != null) {
                sb.append(computeProviderTypeCol + " = '"
                        + acct.getComputeProviderType() + "' AND ");
            }
            if (acct.hasComputeProviderId()) {
                sb.append(computeProviderIdCol + " = "
                        + acct.getComputeProviderId() + " AND ");
            }
            if (acct.hasComputeCredentialId()) {
                sb.append(credentialIdCol + " = "
                        + acct.getComputeCredentialId() + " AND ");
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

    public static TableSpec getTableSpec() {
        TableSpec ts = new TableSpec();
        ts.setTableName(tablename);
        ts.setPrimaryKey(idCol);
        ts.setDdl(ddl);
        return ts;
    }

    public ComputeAcct findComputeAcct(String acctId) throws Exception {
        // FIXME: awoods
        // TODO Auto-generated method stub
        return null;
    }

}
