
package org.duraspace.mainwebapp.home;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.control.LoginController;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.domain.model.User;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManager;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManagerImpl;
import org.easymock.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import junit.framework.TestCase;

import static org.easymock.EasyMock.expect;

public class LoginControllerTest
        extends TestCase {

    private MockHttpServletRequest request;

    private HttpServletResponse response;

    private final String successView = "success/view";

    LoginController controller;

    private DuraSpaceAcctManager dsAcctManager;

    private Credential cred;

    private final String username = "username";

    private final String password = "password";

    private DuraSpaceAcct duraAcct;

    private final String duraAcctName = "duraAcctName";

    private final int duraAcctId = 123;

    private final List<User> users = Arrays.asList(new User(), new User());

    private final List<ComputeAcct> computeAccts =
            Arrays.asList(new ComputeAcct());

    private final List<StorageAcct> storageAccts =
            Arrays.asList(new StorageAcct());

    @Override
    @Before
    protected void setUp() throws Exception {
        controller = new LoginController();
        dsAcctManager = new DuraSpaceAcctManagerImpl();
        dsAcctManager = EasyMock.createMock(DuraSpaceAcctManager.class);

        cred = new Credential();
        cred.setUsername(username);
        cred.setPassword(password);
        duraAcct = new DuraSpaceAcct();
        duraAcct.setAccountName(duraAcctName);
        duraAcct.setId(duraAcctId);

        request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/login.htm");
        request.addParameter("username", cred.getUsername());
        request.addParameter("password", cred.getPassword());

        response = new MockHttpServletResponse();
    }

    @Override
    @After
    protected void tearDown() {
        controller = null;
        dsAcctManager = null;
        cred = null;
        duraAcct = null;

        request = null;
        response = null;
    }

    @Test
    public void testHandleRequestView() throws Exception {
        setUpHappyDay();

        controller.setDuraSpaceAcctManager(dsAcctManager);
        controller.setSuccessView(successView);

        ModelAndView modelAndView = controller.handleRequest(request, response);
        assertNotNull(modelAndView);

        String viewName = modelAndView.getViewName();
        assertEquals(successView, viewName);
    }

    private void setUpHappyDay() throws Exception {
        expect(dsAcctManager.findDuraSpaceAccount(cred)).andReturn(duraAcct);
        expect(dsAcctManager.findDuraSpaceAcctById(duraAcctId)).andReturn(duraAcct);
        expect(dsAcctManager.findUsers(duraAcctId)).andReturn(users);
        expect(dsAcctManager.findComputeAccounts(duraAcctId))
                .andReturn(computeAccts);
        expect(dsAcctManager.findStorageAccounts(duraAcctId))
                .andReturn(storageAccts);
        dsAcctManager.verifyCredential(cred);
        EasyMock.replay(dsAcctManager);
    }
}