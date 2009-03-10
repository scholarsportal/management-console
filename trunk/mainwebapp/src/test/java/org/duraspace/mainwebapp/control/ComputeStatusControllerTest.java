
package org.duraspace.mainwebapp.control;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.mainwebapp.domain.cmd.ComputeAcctWrapper;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.repo.ComputeAcctRepository;
import org.duraspace.mainwebapp.domain.repo.CustomerAcctRepository;
import org.duraspace.mainwebapp.mgmt.ComputeManagerImpl;
import org.duraspace.serviceprovider.mgmt.ComputeProviderFactory;
import org.easymock.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static junit.framework.Assert.assertNotNull;

/**
 * @author Andrew Woods
 */
public class ComputeStatusControllerTest {

    private ComputeStatusController control;

    private Map<String, String> idToComputeProviderMap;

    private final String mockProvider =
            "org.duraspace.serviceprovider.mgmt.mock.MockComputeProviderImpl";

    private final String computeProviderId = "mock-provider";

    private MockHttpServletRequest request;

    private HttpServletResponse response;

    private final String computeAcctIdParamName = "computeAcctId";

    private final String computeAcctId = "compute-acct-id0";

    private final String mockInstanceId = "mockInstanceId";

    private final String cmdParamName = "cmd";

    private final String initCmd = "View Compute Console";

    private ComputeAcctWrapper acctWrapper;

    private ComputeAcct computeAcct;

    private final String viewName = "acctUpdate/computeStatus";

    private final String beanName = "input";

    @Before
    public void setUp() throws Exception {
        acctWrapper = new ComputeAcctWrapper();

        computeAcct = new ComputeAcct();
        computeAcct.setComputeProviderId(computeProviderId);

        control = new ComputeStatusController();

        request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/computeStatus.htm");
        request.setParameter(computeAcctIdParamName, computeAcctId);

        response = new MockHttpServletResponse();

        idToComputeProviderMap = new HashMap<String, String>();
        idToComputeProviderMap.put(computeProviderId, mockProvider);
        ComputeProviderFactory.setIdToClassMap(idToComputeProviderMap);

    }

    @After
    public void tearDown() throws Exception {
        acctWrapper = null;
        computeAcct = null;
        control = null;
        request = null;
        response = null;
        idToComputeProviderMap = null;
    }

    @Test
    public void testInitRequest() throws Exception {

        setUpControllerInit(mockInstanceId);
        request.setParameter(cmdParamName, initCmd);

        acctWrapper = sendRequest(request);
        assertNotNull(acctWrapper);
        assertTrue(acctWrapper.isComputeAppInitialized());

    }

    @SuppressWarnings("unchecked")
    private ComputeAcctWrapper sendRequest(HttpServletRequest req)
            throws Exception {

        ModelAndView mav = control.handleRequest(req, response);
        assertNotNull(mav);

        String name = mav.getViewName();
        assertNotNull(name);
        assertEquals(name, viewName);

        Map<String, Object> model = mav.getModel();
        assertNotNull(model);

        return (ComputeAcctWrapper) model.get(beanName);
    }

    private void setUpControllerInit(String instanceId) throws Exception {
        ComputeManagerImpl mgrImpl = new ComputeManagerImpl();

        computeAcct.setInstanceId(instanceId);

        ComputeAcctRepository computeAcctRepo =
                EasyMock.createMock(ComputeAcctRepository.class);
        expect(computeAcctRepo.findComputeAcct(computeAcctId))
                .andReturn(computeAcct);
        replay(computeAcctRepo);

        CustomerAcctRepository customerAcctRepo =
                EasyMock.createMock(CustomerAcctRepository.class);
        replay(customerAcctRepo);

        mgrImpl.setComputeAcctRepository(computeAcctRepo);
        mgrImpl.setCustomerAcctRepository(customerAcctRepo);

        control.setComputeManager(mgrImpl);
    }

}
