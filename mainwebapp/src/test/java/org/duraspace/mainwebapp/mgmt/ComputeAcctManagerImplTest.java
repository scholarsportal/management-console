
package org.duraspace.mainwebapp.mgmt;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.repo.ComputeAcctRepository;
import org.duraspace.mainwebapp.domain.repo.ComputeProviderRepository;
import org.duraspace.serviceprovider.domain.ComputeProviderType;
import org.duraspace.serviceprovider.mgmt.ComputeProviderFactory;
import org.easymock.EasyMock;

import junit.framework.Assert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static junit.framework.Assert.fail;

public class ComputeAcctManagerImplTest {

    private ComputeAcctManagerImpl mgr;

    private ComputeAcctRepository computeAcctRepo;

    private ComputeProviderRepository computeProviderRepo;

    private CredentialManager credentialMgr;

    private ComputeAcct computeAcct;

    private final int computeAcctId = 111;

    private final int duraAcctId = 222;

    private final String MOCK_PROVIDER =
            ComputeProviderType.AMAZON_EC2.toString();

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

        mgr = new ComputeAcctManagerImpl();
        computeAcctRepo = EasyMock.createMock(ComputeAcctRepository.class);
        computeProviderRepo =
                EasyMock.createMock(ComputeProviderRepository.class);
        credentialMgr = EasyMock.createMock(CredentialManager.class);

        mgr.setComputeAcctRepository(computeAcctRepo);
        mgr.setComputeProviderRepository(computeProviderRepo);
        mgr.setCredentialManager(credentialMgr);

        computeAcct = new ComputeAcct();
        computeAcct.setId(computeAcctId);
        computeAcct.setComputeProviderType(MOCK_PROVIDER);

        credential = new Credential();
        credential.setUsername(username);
        credential.setPassword(password);

    }

    @After
    public void tearDown() throws Exception {
        mgr = null;
        computeAcctRepo = null;
        credential = null;
    }

    @Test
    public void testStartComputeInstance() throws Exception {
        setUp_testStartComputeInstance();
        verifyComputeInstanceNotRunning(computeAcctId);

        mgr.startComputeInstance(computeAcctId);

        verifyComputeInstanceRunning(computeAcctId);
    }

    private void setUp_testStartComputeInstance() throws Exception {
        EasyMock.expect(computeAcctRepo.findComputeAcctById(computeAcctId))
                .andReturn(computeAcct).times(3);
        EasyMock.expect(computeAcctRepo.saveComputeAcct(computeAcct))
                .andReturn(1);
        EasyMock.replay(computeAcctRepo);
    }

    @Test
    public void testFindComputeAcct() throws Exception {
        setUp_testFindComputeAcct();

        ComputeAcct computeAcct = mgr.findComputeAccount(computeAcctId);
        assertNotNull(computeAcct);
    }

    private void setUp_testFindComputeAcct() throws Exception {
        EasyMock.expect(computeAcctRepo.findComputeAcctById(computeAcctId))
                .andReturn(computeAcct);
        EasyMock.replay(computeAcctRepo);
    }

    @Test
    public void testFindComputeAcctByDuraAcctId() throws Exception {
        setUp_testFindComputeAcctByDuraAcctId();

        ComputeAcct computeAcct =
                mgr.findComputeAccountByDuraAcctId(duraAcctId);
        assertNotNull(computeAcct);

    }

    private void setUp_testFindComputeAcctByDuraAcctId() throws Exception {
        EasyMock
                .expect(computeAcctRepo.findComputeAcctByDuraAcctId(duraAcctId))
                .andReturn(computeAcct);
        EasyMock.replay(computeAcctRepo);
    }

    @Test
    public void testDoubleStartComputeInstance() throws Exception {
        setUp_testDoubleStartComputeInstance();
        verifyComputeInstanceNotRunning(computeAcctId);

        mgr.startComputeInstance(computeAcctId);
        try {
            mgr.startComputeInstance(computeAcctId);
            Assert.fail("Should throw exception: not allowed to start twice.");
        } catch (Exception e) {
        }

        verifyComputeInstanceRunning(computeAcctId);
    }

    private void setUp_testDoubleStartComputeInstance() throws Exception {
        EasyMock.expect(computeAcctRepo.findComputeAcctById(computeAcctId))
                .andReturn(computeAcct).times(3);
        EasyMock.expect(computeAcctRepo.saveComputeAcct(computeAcct))
                .andReturn(1);
        EasyMock.expect(computeAcctRepo.findComputeAcctById(computeAcctId))
                .andReturn(computeAcct);
        EasyMock.replay(computeAcctRepo);
    }

    @Test
    public void testStopComputeInstance() throws Exception {
        setUp_testStopComputeInstance();
        verifyComputeInstanceNotRunning(computeAcctId);

        mgr.startComputeInstance(computeAcctId);

        verifyComputeInstanceRunning(computeAcctId);

        mgr.stopComputeInstance(computeAcctId);

        verifyComputeInstanceNotRunning(computeAcctId);

        // Double stop should be fine.
        try {
            mgr.stopComputeInstance(computeAcctId);
        } catch (Exception e) {
            fail("Should not throw exception for double stopping.");
        }
    }

    private void setUp_testStopComputeInstance() throws Exception {
        EasyMock.expect(computeAcctRepo.findComputeAcctById(computeAcctId))
                .andReturn(computeAcct).times(6);
        EasyMock.expect(computeAcctRepo.saveComputeAcct(computeAcct))
                .andReturn(1).times(3);
        EasyMock.replay(computeAcctRepo);
    }

    @Test
    public void testRefresh() throws Exception {
        setUp_testRefresh();
        assertNotNull(mgr.findComputeAccount(computeAcctId));
    }

    private void setUp_testRefresh() throws Exception {
        EasyMock.expect(computeAcctRepo.findComputeAcctById(computeAcctId))
                .andReturn(computeAcct);
        EasyMock.replay(computeAcctRepo);
    }

    private void verifyComputeInstanceRunning(int acctId) throws Exception {
        ComputeAcct computeAcct = mgr.findComputeAccount(acctId);
        assertNotNull(computeAcct);
        assertTrue(computeAcct.isInstanceRunning());
    }

    private void verifyComputeInstanceNotRunning(int acctId) throws Exception {
        ComputeAcct computeAcct = mgr.findComputeAccount(acctId);
        assertNotNull(computeAcct);
        assertTrue(!computeAcct.isInstanceRunning());
    }

}
