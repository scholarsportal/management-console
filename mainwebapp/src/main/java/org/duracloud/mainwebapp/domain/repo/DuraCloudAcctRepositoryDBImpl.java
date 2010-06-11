package org.duracloud.mainwebapp.domain.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.duracloud.common.model.Credential;
import org.duracloud.common.util.TableSpec;
import org.duracloud.mainwebapp.domain.model.DuraCloudAcct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class DuraCloudAcctRepositoryDBImpl
        extends SimpleJdbcDaoSupport
        implements DuraCloudAcctRepository {

    protected final Logger log = LoggerFactory.getLogger(DuraCloudAcctRepositoryDBImpl.class);

    private final static String tablename = "DuraCloudAcct";

    private final static String idCol = "id";

    private final static String acctNameCol = "accountName";

    private final static String billingInfoIdCol = "billingInfo_id";

    private final String DURACLOUDACCT_INSERT =
            "INSERT INTO " + tablename + " (" + acctNameCol + ","
                    + billingInfoIdCol + ") " + "VALUES (:" + acctNameCol
                    + ",:" + billingInfoIdCol + ")";

    private final String DURACLOUDACCT_SELECT =
            "SELECT " + idCol + "," + acctNameCol + "," + billingInfoIdCol
                    + " FROM " + tablename;

    private final String DURACLOUDACCT_SELECT_BY_ID =
            DURACLOUDACCT_SELECT + " WHERE " + idCol + " = ?";

    private final String DURACLOUDACCT_SELECT_BY_NAME =
            DURACLOUDACCT_SELECT + " WHERE " + acctNameCol + " = ?";

    private final String ID_SELECT = "SELECT " + idCol + " FROM " + tablename;

    private final String ID_SELECT_FOR_SINGLE_DURACLOUDACCT =
            ID_SELECT + " WHERE ";

    // Leaving DDL hard-coded for legibility.
    private final static String ddl =
            "CREATE TABLE DuraCloudAcct ("
                    + "id INT GENERATED ALWAYS AS IDENTITY,"
                    + "accountName VARCHAR(256) NOT NULL UNIQUE,"
                    + "billingInfo_id INT," + "PRIMARY KEY (id))";

    /**
     * {@inheritDoc}
     */
    public int saveDuraAcct(DuraCloudAcct duraAcct) throws Exception {
        if (existsInTable(duraAcct)) {
            return doUpdate(duraAcct);
        } else {
            return doInsert(duraAcct);
        }
    }

    private boolean existsInTable(DuraCloudAcct duraAcct) {
        DuraCloudAcct existingDuraAcct = null;
        try {
            existingDuraAcct = findDuraAcctById(findIdFor(duraAcct));
        } catch (Exception e) {
        }
        return existingDuraAcct != null;
    }

    @SuppressWarnings("unchecked")
    private int doUpdate(DuraCloudAcct duraAcct) throws Exception {
        StringBuilder sb = new StringBuilder("UPDATE " + tablename + " SET ");
        int len = sb.length();
        if (duraAcct.getAccountName() != null) {
            sb.append(acctNameCol + " = '" + duraAcct.getAccountName() + "', ");
        }
        if (duraAcct.hasBillingInfoId()) {
            sb.append(billingInfoIdCol + " = " + duraAcct.getBillingInfoId()
                    + ", ");
        }

        // Verify need for update.
        if (sb.length() == len) {
            throw new Exception("Nothing to update, all fields are null!");
        }

        sb.deleteCharAt(sb.lastIndexOf(", "));
        sb.append("WHERE " + idCol + " = " + duraAcct.getId() + "");

        log.debug("Executing update: '" + sb.toString() + "'");
        this.getSimpleJdbcTemplate().update(sb.toString(), new HashMap());

        return findIdFor(duraAcct);
    }

    private int doInsert(DuraCloudAcct duraAcct) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(acctNameCol, duraAcct.getAccountName());
        if (duraAcct.hasBillingInfoId()) {
            params.put(billingInfoIdCol, duraAcct.getBillingInfoId());
        } else {
            params.put(billingInfoIdCol, null);
        }

        this.getSimpleJdbcTemplate().update(DURACLOUDACCT_INSERT, params);

        return findIdFor(duraAcct);
    }

    public DuraCloudAcct findDuraAcctById(int id) throws Exception {
        List<DuraCloudAcct> duraAccts =
                this.findDuraCloudAcct(this.DURACLOUDACCT_SELECT_BY_ID, id);

        if (duraAccts.size() == 0) {
            throw new Exception(tablename + " not found with id: '" + id + "'");
        }
        if (duraAccts.size() != 1) {
            throw new Exception(tablename + " more than 1 acct with id: '" + id
                    + "'");
        }

        return duraAccts.get(0);
    }

    public DuraCloudAcct findDuraAcctByName(String duraAcctName)
            throws Exception {
        List<DuraCloudAcct> duraAccts =
                this.findDuraCloudAcct(this.DURACLOUDACCT_SELECT_BY_NAME,
                                       duraAcctName);

        if (duraAccts.size() == 0) {
            throw new Exception(tablename + " not found with acctName: '"
                    + duraAcctName + "'");
        }
        if (duraAccts.size() != 1) {
            throw new Exception(tablename
                    + " more than 1 acct with acctName: '" + duraAcctName + "'");
        }

        return duraAccts.get(0);
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> getDuraAcctIds() throws Exception {
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

    public DuraCloudAcct findDuraCloudAcct(Credential cred) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    private List<DuraCloudAcct> findDuraCloudAcct(String query,
                                                  Object... params) {
        List<DuraCloudAcct> duraAccts =
                this.getSimpleJdbcTemplate()
                        .query(query,
                               new ParameterizedRowMapper<DuraCloudAcct>() {

                                   public DuraCloudAcct mapRow(ResultSet rs,
                                                               int rowNum)
                                           throws SQLException {
                                       DuraCloudAcct duraAcct =
                                               new DuraCloudAcct();
                                       duraAcct.setId(rs.getInt(idCol));
                                       duraAcct.setAccountName(rs
                                               .getString(acctNameCol));
                                       duraAcct.setBillingInfoId(rs
                                               .getInt(billingInfoIdCol));
                                       return duraAcct;
                                   }
                               },
                               params);
        return duraAccts;
    }

    @SuppressWarnings("unchecked")
    private int findIdFor(DuraCloudAcct duraAcct) throws Exception {
        Integer id = null;
        if (duraAcct.hasId()) {
            id = duraAcct.getId();
        } else {
            StringBuilder sb =
                    new StringBuilder(ID_SELECT_FOR_SINGLE_DURACLOUDACCT + " (");
            int len = sb.length();
            if (duraAcct.getAccountName() != null) {
                sb.append(acctNameCol + " = '" + duraAcct.getAccountName()
                        + "' AND ");
            }
            if (duraAcct.hasBillingInfoId()) {
                sb.append(billingInfoIdCol + " = "
                        + duraAcct.getBillingInfoId() + " AND ");
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
            throw new Exception("ID not found for : '" + duraAcct + "'");
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
