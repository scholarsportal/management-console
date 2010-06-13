/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.common.model.Credential;
import org.duracloud.common.util.DatabaseUtil;
import org.duracloud.mainwebapp.domain.model.Address;
import org.duracloud.mainwebapp.domain.repo.db.MainDatabaseUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestAddressRepositoryDBImpl {

    private final DatabaseUtil dbUtil;

    private final String baseDir = "testMainDB";

    private final Credential dbCred = new Credential("duracloud", "duracloud");

    private AddressRepositoryDBImpl repo;

    private final String tablename =
            AddressRepositoryDBImpl.getTableSpec().getTableName();

    private Address addrA;

    private final String apt = "apt";

    private final String city = "city";

    private final String state = "state";

    private final String street1 = "street1";

    private final String street2 = "street2";

    private final String zip = "22201";

    public TestAddressRepositoryDBImpl()
            throws Exception {
        dbUtil = new MainDatabaseUtil(dbCred, baseDir);
        dbUtil.initializeDB();
    }

    @Before
    public void setUp() throws Exception {
        repo = new AddressRepositoryDBImpl();
        repo.setDataSource(dbUtil.getDataSource());

        addrA = new Address();
        addrA.setApt(apt);
        addrA.setCity(city);
        addrA.setState(state);
        addrA.setStreet1(street1);
        addrA.setStreet2(street2);
        addrA.setZip(zip);
    }

    @After
    public void tearDown() throws Exception {
        repo = null;
        addrA = null;
        dbUtil.clearDB();
    }

    @Test
    public void testSaveAddress() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        int id = repo.saveAddress(addrA);

        verifyTableSize(4);

        Address addr = repo.findAddressById(id);
        assertNotNull(addr);
        assertEquals(apt, addr.getApt());
        assertEquals(city, addr.getCity());
        assertEquals(state, addr.getState());
        assertEquals(street1, addr.getStreet1());
        assertEquals(street2, addr.getStreet2());
        assertEquals(zip, addr.getZip());

    }

    @Test
    public void testSaveWithPartialAddress() throws Exception {
        verifyTableSize(0);
        insertTestData(3);
        verifyTableSize(3);

        Address addr = new Address();
        addr.setStreet1(street1);
        addr.setCity(city);
        addr.setState(state);
        addr.setZip(zip);
        int id = repo.saveAddress(addr);

        verifyTableSize(4);

        Address addrFound = repo.findAddressById(id);
        assertNotNull(addrFound);
        assertEquals(null, addrFound.getApt());
        assertEquals(city, addrFound.getCity());
        assertEquals(state, addrFound.getState());
        assertEquals(street1, addrFound.getStreet1());
        assertEquals(null, addrFound.getStreet2());
        assertEquals(zip, addrFound.getZip());

    }

    @Test
    public void testBadRetrieval() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<Integer> ids = repo.getAddressIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        Address addr = null;
        try {
            addr = repo.findAddressById(-99);
            Assert.fail("Should throw exception.");
        } catch (Exception e) {
        }
        assertTrue(addr == null);
    }

    @Test
    public void testUpdates() throws Exception {
        verifyTableSize(0);
        insertTestData(2);
        verifyTableSize(2);

        List<Integer> ids = repo.getAddressIds();
        assertNotNull(ids);
        assertTrue(ids.size() == 2);

        for (Integer id : ids) {
            // Get Address
            Address addr = repo.findAddressById(id);
            assertNotNull(addr);
            String s1 = addr.getStreet1();
            String s2 = addr.getStreet2();
            String ap = addr.getApt();
            String ci = addr.getCity();
            String st = addr.getState();
            String zi = addr.getZip();
            assertNotNull(s1);
            assertNotNull(s2);
            assertNotNull(ap);
            assertNotNull(ci);
            assertNotNull(st);
            assertNotNull(zi);

            // Save same address with some updates.
            Address addrNew = new Address();
            String s1New = s1 + "test";
            String s2New = s2;
            String apNew = ap + "test";
            String ciNew = ci + "test";
            String stNew = st + "test";
            String ziNew = zi;
            addrNew.setStreet1(s1New);
            addrNew.setStreet2(s2New);
            addrNew.setApt(apNew);
            addrNew.setCity(ciNew);
            addrNew.setState(stNew);
            addrNew.setZip(ziNew);

            // Setting the ID is how the update happens.
            addrNew.setId(id);
            repo.saveAddress(addrNew);

            // Check updates.
            Address addrUpdated = repo.findAddressById(id);
            assertNotNull(addrUpdated);
            assertEquals(s1New, addrUpdated.getStreet1());
            assertEquals(s2New, addrUpdated.getStreet2());
            assertEquals(apNew, addrUpdated.getApt());
            assertEquals(ciNew, addrUpdated.getCity());
            assertEquals(stNew, addrUpdated.getState());
            assertEquals(ziNew, addrUpdated.getZip());
        }
    }

    @Test
    public void testPartialUpdate() throws Exception {
        verifyTableSize(0);

        // Insert initial item
        Address addr = new Address();
        addr.setStreet1(street1);
        addr.setCity(city);
        addr.setState(state);
        addr.setZip(zip);
        int id = repo.saveAddress(addr);

        verifyTableSize(1);

        // Verify item.
        Address addrFound = repo.findAddressById(id);
        assertNotNull(addrFound);
        assertEquals(street1, addrFound.getStreet1());
        assertEquals(city, addrFound.getCity());
        assertEquals(state, addrFound.getState());
        assertEquals(zip, addrFound.getZip());
        assertTrue(addrFound.getStreet2() == null);
        assertTrue(addrFound.getApt() == null);

        // Do partial update
        String cityNew = "cityNew";
        String stateNew = "stateNew";
        Address addrUpdated = new Address();
        addrUpdated.setId(id);
        addrUpdated.setCity(cityNew);
        addrUpdated.setState(stateNew);

        // Push update.
        repo.saveAddress(addrUpdated);

        // Verify updates.
        Address addrVerify = repo.findAddressById(id);
        assertNotNull(addrVerify);
        assertEquals(street1, addrVerify.getStreet1());
        assertEquals(cityNew, addrVerify.getCity());
        assertEquals(stateNew, addrVerify.getState());
        assertEquals(zip, addrVerify.getZip());
        assertTrue(addrVerify.getStreet2() == null);
        assertTrue(addrVerify.getApt() == null);
    }

    @Test
    public void testInsert() throws Exception {
        verifyTableSize(0);
        int id = 1000;

        // Make sure address not found with given id.
        Address addr = null;
        try {
            addr = repo.findAddressById(id);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(addr == null);

        addr = new Address();
        addr.setId(id);
        addr.setStreet1(street1);
        addr.setCity(city);
        addr.setState(state);
        addr.setZip(zip);
        repo.saveAddress(addr);

        // Check that ID was ignored.
        verifyTableSize(1);
        Address addrFound = null;
        try {
            addrFound = repo.findAddressById(id);
            fail("should have thrown exception.");
        } catch (Exception e) {
        }
        assertTrue(addrFound == null);
    }

    @Test
    public void testEmptyInsert() throws Exception {
        verifyTableSize(0);

        Address addr = new Address();

        // Should throw if on content.
        try {
            repo.saveAddress(addr);
            fail("should have thrown exception.");
        } catch (Exception e1) {
        }

        verifyTableSize(0);
    }

    @SuppressWarnings("unchecked")
    private void verifyTableSize(int size) {
        List results =
                dbUtil.getOps().queryForList("SELECT * FROM " + tablename);
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == size);
    }

    private void insertTestData(int size) {

        for (int i = 0; i < size; ++i) {
            dbUtil.getOps()
                    .update("INSERT INTO " + tablename
                            + " (street1,street2,apt,city,state,zip) VALUES ("
                            + "'" + street1 + i + "','" + street2 + i + "','"
                            + apt + i + "','" + city + i + "','" + state + i
                            + "','" + zip + "')");
        }
    }

}
