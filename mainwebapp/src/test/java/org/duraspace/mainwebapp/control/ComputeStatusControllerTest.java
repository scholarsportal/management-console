
package org.duraspace.mainwebapp.control;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.computeprovider.domain.ComputeProviderType;
import org.duraspace.computeprovider.mgmt.ComputeProviderFactory;
import org.duraspace.mainwebapp.domain.cmd.ComputeAcctWrapper;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.ComputeProvider;
import org.duraspace.mainwebapp.domain.repo.ComputeAcctRepository;
import org.duraspace.mainwebapp.domain.repo.ComputeProviderRepository;
import org.duraspace.mainwebapp.mgmt.ComputeAcctManagerImpl;
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

    private final ComputeProviderType computeProviderType =
            ComputeProviderType.UNKNOWN;

    private final int computeProviderId = 111;

    private MockHttpServletRequest request;

    private HttpServletResponse response;

    private final String computeAcctIdParamName = "computeAcctId";

    private final Integer computeAcctId = 1234;

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
        computeAcct.setComputeProviderType(computeProviderType);
        computeAcct.setComputeProviderId(computeProviderId);

        control = new ComputeStatusController();

        request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/computeStatus.htm");
        request.setParameter(computeAcctIdParamName, computeAcctId.toString());

        response = new MockHttpServletResponse();

        idToComputeProviderMap = new HashMap<String, String>();
        idToComputeProviderMap
                .put(computeProviderType.toString(), mockProvider);
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
        request.setParameter(computeAcctIdParamName, computeAcctId.toString());

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
        ComputeAcctManagerImpl mgrImpl = new ComputeAcctManagerImpl();

        ComputeProvider computeProvider = new ComputeProvider();

        ComputeProviderRepository computeProviderRepo =
                EasyMock.createMock(ComputeProviderRepository.class);
        expect(computeProviderRepo.findComputeProviderById(computeProviderId))
                .andReturn(computeProvider);
        replay(computeProviderRepo);

        computeAcct.setInstanceId(instanceId);
        computeAcct.setComputeProviderId(computeProviderId);

        ComputeAcctRepository computeAcctRepo =
                EasyMock.createMock(ComputeAcctRepository.class);
        expect(computeAcctRepo.findComputeAcctById(computeAcctId))
                .andReturn(computeAcct).times(3);
        replay(computeAcctRepo);

        mgrImpl.setComputeAcctRepository(computeAcctRepo);
        mgrImpl.setComputeProviderRepository(computeProviderRepo);

        control.setComputeManager(mgrImpl);
    }

}
