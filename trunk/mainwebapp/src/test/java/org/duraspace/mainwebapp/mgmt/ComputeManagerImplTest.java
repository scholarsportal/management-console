
package org.duraspace.mainwebapp.mgmt;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.CustomerAcct;
import org.duraspace.mainwebapp.domain.repo.ComputeAcctRepositoryFileImpl;
import org.duraspace.mainwebapp.domain.repo.CustomerAcctRepositoryFileImpl;
import org.duraspace.serviceprovider.mgmt.ComputeProviderFactory;

import junit.framework.Assert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static junit.framework.Assert.fail;

public class ComputeManagerImplTest {

    private ComputeManagerImpl mgr;

    private ComputeAcctRepositoryFileImpl computeRepo;

    private CustomerAcctRepositoryFileImpl customerRepo;

    private final String testRepoLocKey =
            CustomerAcctRepositoryFileImpl.REPO_LOCATION;

    private final String testComputeRepoLoc = "testComputeAcctRepo.xml";

    private final String testCustomerRepoLoc = "testCustomerAcctRepo.xml";

    private final String MOCK_PROVIDER = "mockProvider";

    private final String MOCK_PROVIDER_CLASSNAME =
            "org.duraspace.serviceprovider.mgmt.mock.MockComputeProviderImpl";

    private Credential credential;

    private final String username = "username";

    private final String password = "password";

    @Before
    public void setUp() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put(MOCK_PROVIDER, MOCK_PROVIDER_CLASSNAME);
        ComputeProviderFactory.setIdToClassMap(map);

        mgr = new ComputeManagerImpl();
        computeRepo = new ComputeAcctRepositoryFileImpl();
        customerRepo = new CustomerAcctRepositoryFileImpl();

        Properties computeAcctProps = new Properties();
        computeAcctProps.setProperty(testRepoLocKey, testComputeRepoLoc);
        computeRepo.setProperties(computeAcctProps);

        Properties customerAcctProps = new Properties();
        customerAcctProps.setProperty(testRepoLocKey, testCustomerRepoLoc);
        customerRepo.setProperties(customerAcctProps);

        mgr.setComputeAcctRepository(computeRepo);
        mgr.setCustomerAcctRepository(customerRepo);

        credential = new Credential();
        credential.setUsername(username);
        credential.setPassword(password);

    }

    @After
    public void tearDown() throws Exception {
        mgr = null;
        computeRepo = null;
        customerRepo = null;
        credential = null;
    }

    @Test
    public void testStartComputeInstance() throws Exception {
        verifyComputeInstanceNotRunning(credential);

        mgr.startComputeInstance(credential);

        verifyComputeInstanceRunning(credential);
    }

    private void verifyComputeInstanceNotRunning(Credential cred)
            throws Exception {
        ComputeAcct computeAcct = findComputeAcct(cred);
        assertTrue(!computeAcct.isInstanceRunning());
    }

    private void verifyComputeInstanceRunning(Credential cred) throws Exception {
        ComputeAcct computeAcct = findComputeAcct(cred);
        assertTrue(computeAcct.isInstanceRunning());
    }

    private ComputeAcct findComputeAcct(Credential cred) throws Exception {
        CustomerAcct customerAcct = customerRepo.findCustomerAcct(cred);
        assertNotNull(customerAcct);

        String computeAcctId = customerAcct.getComputeAcctId();
        assertNotNull(computeAcctId);

        ComputeAcct computeAcct = computeRepo.findComputeAcct(computeAcctId);
        assertNotNull(computeAcct);

        return computeAcct;
    }

    @Test
    public void testDoubleStartComputeInstance() throws Exception {
        verifyComputeInstanceNotRunning(credential);

        mgr.startComputeInstance(credential);
        try {
            mgr.startComputeInstance(credential);
            Assert.fail("Should throw exception: not allowed to start twice.");
        } catch (Exception e) {
        }

        verifyComputeInstanceRunning(credential);
    }

    @Test
    public void testStopComputeInstance() throws Exception {
        verifyComputeInstanceNotRunning(credential);

        mgr.startComputeInstance(credential);

        verifyComputeInstanceRunning(credential);

        mgr.stopComputeInstance(credential);

        verifyComputeInstanceNotRunning(credential);

        // Double stop should be fine.
        try {
            mgr.stopComputeInstance(credential);
        } catch (Exception e) {
            fail("Should not throw exception for double stopping.");
        }
    }

    @Test
    public void testRefresh() throws Exception {
        assertNotNull(mgr.findComputeAccount(credential));
    }

}
