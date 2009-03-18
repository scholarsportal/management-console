
package org.duraspace.mainwebapp.domain.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.TableSpec;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class DuraSpaceAcctRepositoryDBImpl
        extends SimpleJdbcDaoSupport
        implements DuraSpaceAcctRepository {

    protected final Logger log = Logger.getLogger(getClass());

    private final static String tablename = "DuraSpaceAcct";

    private final static String idCol = "id";

    private final static String acctNameCol = "accountName";

    private final static String billingInfoIdCol = "billingInfo_id";

    private final String DURASPACEACCT_INSERT =
            "INSERT INTO " + tablename + " (" + acctNameCol + ","
                    + billingInfoIdCol + ") " + "VALUES (:" + acctNameCol
                    + ",:" + billingInfoIdCol + ")";

    private final String DURASPACEACCT_SELECT =
            "SELECT " + idCol + "," + acctNameCol + "," + billingInfoIdCol
                    + " FROM " + tablename;

    private final String DURASPACEACCT_SELECT_BY_ID =
            DURASPACEACCT_SELECT + " WHERE " + idCol + " = ?";

    private final String DURASPACEACCT_SELECT_BY_NAME =
            DURASPACEACCT_SELECT + " WHERE " + acctNameCol + " = ?";

    private final String ID_SELECT = "SELECT " + idCol + " FROM " + tablename;

    private final String ID_SELECT_FOR_SINGLE_DURASPACEACCT =
            ID_SELECT + " WHERE ";

    // Leaving DDL hard-coded for legibility.
    private final static String ddl =
            "CREATE TABLE DuraSpaceAcct ("
                    + "id INT GENERATED ALWAYS AS IDENTITY,"
                    + "accountName VARCHAR(256) NOT NULL UNIQUE,"
                    + "billingInfo_id INT," + "PRIMARY KEY (id))";

    /**
     * {@inheritDoc}
     */
    public int saveDuraAcct(DuraSpaceAcct duraAcct) throws Exception {
        if (existsInTable(duraAcct)) {
            return doUpdate(duraAcct);
        } else {
            return doInsert(duraAcct);
        }
    }

    private boolean existsInTable(DuraSpaceAcct duraAcct) {
        DuraSpaceAcct existingDuraAcct = null;
        try {
            existingDuraAcct = findDuraAcctById(findIdFor(duraAcct));
        } catch (Exception e) {
        }
        return existingDuraAcct != null;
    }

    @SuppressWarnings("unchecked")
    private int doUpdate(DuraSpaceAcct duraAcct) throws Exception {
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

    private int doInsert(DuraSpaceAcct duraAcct) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(acctNameCol, duraAcct.getAccountName());
        if (duraAcct.hasBillingInfoId()) {
            params.put(billingInfoIdCol, duraAcct.getBillingInfoId());
        } else {
            params.put(billingInfoIdCol, null);
        }

        this.getSimpleJdbcTemplate().update(DURASPACEACCT_INSERT, params);

        return findIdFor(duraAcct);
    }

    public DuraSpaceAcct findDuraAcctById(int id) throws Exception {
        List<DuraSpaceAcct> duraAccts =
                this.findDuraSpaceAcct(this.DURASPACEACCT_SELECT_BY_ID, id);

        if (duraAccts.size() == 0) {
            throw new Exception(tablename + " not found with id: '" + id + "'");
        }
        if (duraAccts.size() != 1) {
            throw new Exception(tablename + " more than 1 acct with id: '" + id
                    + "'");
        }

        return duraAccts.get(0);
    }

    public DuraSpaceAcct findDuraAcctByName(String duraAcctName)
            throws Exception {
        List<DuraSpaceAcct> duraAccts =
                this.findDuraSpaceAcct(this.DURASPACEACCT_SELECT_BY_NAME,
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

    public DuraSpaceAcct findDuraSpaceAcct(Credential cred) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    private List<DuraSpaceAcct> findDuraSpaceAcct(String query,
                                                  Object... params) {
        List<DuraSpaceAcct> duraAccts =
                this.getSimpleJdbcTemplate()
                        .query(query,
                               new ParameterizedRowMapper<DuraSpaceAcct>() {

                                   public DuraSpaceAcct mapRow(ResultSet rs,
                                                               int rowNum)
                                           throws SQLException {
                                       DuraSpaceAcct duraAcct =
                                               new DuraSpaceAcct();
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
    private int findIdFor(DuraSpaceAcct duraAcct) throws Exception {
        Integer id = null;
        if (duraAcct.hasId()) {
            id = duraAcct.getId();
        } else {
            StringBuilder sb =
                    new StringBuilder(ID_SELECT_FOR_SINGLE_DURASPACEACCT + " (");
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
