package org.duracloud.mainwebapp.domain.repo;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.duracloud.common.util.TableSpec;
import org.duracloud.mainwebapp.domain.model.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

public class AddressRepositoryDBImpl
        extends SimpleJdbcDaoSupport
        implements AddressRepository {

    protected final Logger log = LoggerFactory.getLogger(AddressRepositoryDBImpl.class);

    private final static String tablename = "Address";

    private final static String idCol = "id";

    private final static String street1Col = "street1";

    private final static String street2Col = "street2";

    private final static String aptCol = "apt";

    private final static String cityCol = "city";

    private final static String stateCol = "state";

    private final static String zipCol = "zip";

    private final String ADDRESS_INSERT =
            "INSERT INTO Address (street1, street2, apt, city, state, zip) "
                    + "VALUES (:street1, :street2, :apt, :city, :state, :zip)";

    private final String ADDRESS_SELECT =
            "SELECT id, street1, street2, apt, city, state, zip FROM Address "
                    + "WHERE id = ?";

    private final String ID_SELECT = "SELECT id FROM Address";

    private final String ID_SELECT_FOR_SINGLE_ADDRESS = ID_SELECT + " WHERE ";

    private final static String ddl =
            "CREATE TABLE Address (" + "id INT GENERATED ALWAYS AS IDENTITY,"
                    + "street1 VARCHAR(64) NOT NULL," + "street2 VARCHAR(64),"
                    + "apt VARCHAR(16)," + "city VARCHAR(64) NOT NULL,"
                    + "state VARCHAR(64) NOT NULL," + "zip CHAR(5) NOT NULL,"
                    + "PRIMARY KEY (id))";

    /**
     * <pre>
     * This method persists the provided Address.
     * If the provided Address contains an ID,
     * ...the existing Address will be over-written;
     * ...otherwise the provided Address will be a new row.
     *
     * Note: If provided Address contains an ID that does not exist
     * in the table, the ID will be ignored.
     * </pre> {@inheritDoc}
     *
     * @throws Exception
     */
    public int saveAddress(Address addr) throws Exception {
        if (existsInTable(addr)) {
            doUpdate(addr);
        } else {
            doInsert(addr);
        }
        return findIdFor(addr);
    }

    private boolean existsInTable(Address addr) {
        Address existingAddr = null;
        try {
            existingAddr = findAddressById(findIdFor(addr));
        } catch (Exception e) {
        }
        return existingAddr != null;
    }

    @SuppressWarnings("unchecked")
    private int doUpdate(Address addr) throws Exception {
        StringBuilder sb = new StringBuilder("UPDATE " + tablename + " SET ");
        int len = sb.length();
        if (addr.getStreet1() != null) {
            sb.append(street1Col + " = '" + addr.getStreet1() + "', ");
        }
        if (addr.getStreet2() != null) {
            sb.append(street2Col + " = '" + addr.getStreet2() + "', ");
        }
        if (addr.getApt() != null) {
            sb.append(aptCol + " = '" + addr.getApt() + "', ");
        }
        if (addr.getCity() != null) {
            sb.append(cityCol + " = '" + addr.getCity() + "', ");
        }
        if (addr.getState() != null) {
            sb.append(stateCol + " = '" + addr.getState() + "', ");
        }
        if (addr.getZip() != null) {
            sb.append(zipCol + " = '" + addr.getZip() + "', ");
        }

        // Verify need for update.
        if (sb.length() == len) {
            throw new Exception("Nothing to update, all fields are null!");
        }

        sb.deleteCharAt(sb.lastIndexOf(", "));
        sb.append("WHERE " + idCol + " = " + addr.getId() + "");

        log.debug("Executing update: '" + sb.toString() + "'");
        this.getSimpleJdbcTemplate().update(sb.toString(), new HashMap());

        return findIdFor(addr);
    }

    private int doInsert(Address addr) throws Exception {
        this.getSimpleJdbcTemplate().update(ADDRESS_INSERT, buildParams(addr));

        return findIdFor(addr);
    }

    /**
     * <pre>
     * This method returns the Address of the provided ID.
     * If no Address is found, an exception is thrown.
     * </pre> {@inheritDoc}
     */
    public Address findAddressById(int id) throws Exception {
        List<Address> addresses =
                this.getSimpleJdbcTemplate()
                        .query(ADDRESS_SELECT,
                               new ParameterizedRowMapper<Address>() {

                                   public Address mapRow(ResultSet rs,
                                                         int rowNum)
                                           throws SQLException {
                                       Address addr = new Address();
                                       addr.setId(rs.getInt(idCol));
                                       addr
                                               .setStreet1(rs
                                                       .getString(street1Col));
                                       addr
                                               .setStreet2(rs
                                                       .getString(street2Col));
                                       addr.setApt(rs.getString(aptCol));
                                       addr.setCity(rs.getString(cityCol));
                                       addr.setState(rs.getString(stateCol));
                                       addr.setZip(rs.getString(zipCol));
                                       return addr;
                                   }
                               },
                               id);
        if (addresses.size() == 0) {
            throw new Exception("Address not found with id: '" + id + "'");
        }
        return addresses.get(0);
    }

    /**
     * <pre>
     * This method returns all IDs in the table.
     * If no results are found, an exception is thrown.
     * </pre> {@inheritDoc}
     */
    public List<Integer> getAddressIds() throws Exception {
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

    @SuppressWarnings("unchecked")
    private int findIdFor(Address addr) throws Exception {
        Integer id = null;
        if (addr.hasId()) {
            id = addr.getId();
        } else {

            StringBuilder sb =
                    new StringBuilder(ID_SELECT_FOR_SINGLE_ADDRESS + " (");
            int len = sb.length();
            if (addr.getStreet1() != null) {
                sb.append(street1Col + " = '" + addr.getStreet1() + "' AND ");
            }
            if (addr.getStreet2() != null) {
                sb.append(street2Col + " = '" + addr.getStreet2() + "' AND ");
            }
            if (addr.getApt() != null) {
                sb.append(aptCol + " = '" + addr.getApt() + "' AND ");
            }
            if (addr.getCity() != null) {
                sb.append(cityCol + " = '" + addr.getCity() + "' AND ");
            }
            if (addr.getState() != null) {
                sb.append(stateCol + " = '" + addr.getState() + "' AND ");
            }
            if (addr.getZip() != null) {
                sb.append(zipCol + " = '" + addr.getZip() + "' AND ");
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
            throw new Exception("ID not found for : '" + addr + "'");
        }
        return id;
    }

    private Map<String, String> buildParams(Address addr) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(street1Col, addr.getStreet1());
        params.put(street2Col, addr.getStreet2());
        params.put(aptCol, addr.getApt());
        params.put(cityCol, addr.getCity());
        params.put(stateCol, addr.getState());
        params.put(zipCol, addr.getZip());
        return params;
    }

    public static TableSpec getTableSpec() {
        TableSpec ts = new TableSpec();
        ts.setTableName(tablename);
        ts.setPrimaryKey(idCol);
        ts.setDdl(ddl);
        return ts;
    }
}
