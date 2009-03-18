
package org.duraspace.mainwebapp.home;

import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.control.LoginController;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManager;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManagerImpl;
import org.easymock.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import junit.framework.TestCase;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

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
        replay(dsAcctManager);
    }
}