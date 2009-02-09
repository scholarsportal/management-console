package org.duraspace.home;

import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.acct.LoginController;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import junit.framework.TestCase;

public class LoginControllerTest extends TestCase {

    private MockHttpServletRequest request;
    private HttpServletResponse response;
    private final String successView = "success/view";

    @Override
    @Before
    protected void setUp()
    {
        request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/login.htm");

        response = new MockHttpServletResponse();
    }

    @Override
    @After
    protected void tearDown()
    {
        request = null;
        response = null;
    }

    @Test
    public void testHandleRequestView() throws Exception{
        LoginController controller = new LoginController();
        controller.setSuccessView(successView);

        ModelAndView modelAndView = controller.handleRequest(request, response);
        assertNotNull(modelAndView);

        String viewName = modelAndView.getViewName();
        assertEquals(successView, viewName);
    }
}