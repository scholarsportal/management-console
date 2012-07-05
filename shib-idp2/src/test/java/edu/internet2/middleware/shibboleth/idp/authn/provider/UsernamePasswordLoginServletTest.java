/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package edu.internet2.middleware.shibboleth.idp.authn.provider;

import junit.framework.Assert;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.common.util.EncryptionUtil;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static edu.internet2.middleware.shibboleth.idp.authn.provider.UsernamePasswordLoginServlet.passwordAttribute;
import static edu.internet2.middleware.shibboleth.idp.authn.provider.UsernamePasswordLoginServlet.usernameAttribute;
import static org.duracloud.common.util.ChecksumUtil.Algorithm.SHA_256;

/**
 * @author Andrew Woods
 *         Date: 7/3/12
 */
public class UsernamePasswordLoginServletTest {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private LoginContext loginContext;

    @Before
    public void setUp() throws Exception {
        request = EasyMock.createMock("Request", HttpServletRequest.class);
        response = EasyMock.createMock("Response", HttpServletResponse.class);
        loginContext = EasyMock.createMock("LoginContext", LoginContext.class);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(request, response, loginContext);
    }

    private void replayMocks() {
        EasyMock.replay(request, response, loginContext);
    }

    @Test
    public void testService() throws Exception {
        String username = "user-name";
        String password = "pass-word";
        EasyMock.expect(request.getParameter(usernameAttribute)).andReturn(
            username);
        EasyMock.expect(request.getParameter(passwordAttribute)).andReturn(
            password);

        replayMocks();

        // Expected password
        ChecksumUtil checksumUtil = new ChecksumUtil(SHA_256);
        String encPassword = checksumUtil.generateChecksum(password);

        UsernamePasswordLoginServletWrapper servlet =
            new UsernamePasswordLoginServletWrapper(username, encPassword);

        // Perform the test
        try {
            servlet.service(request, response);
        } catch (RuntimeException e) {
            Assert.assertEquals("test completed", e.getMessage());
        }

        Assert.assertTrue(servlet.isCalled());
    }

    /**
     * This private classes inherits from the actual class under test:
     * -- UsernamePasswordLoginServlet --
     */
    private class UsernamePasswordLoginServletWrapper extends UsernamePasswordLoginServlet {
        private String expectedUsername;
        private String expectedPassword;

        private boolean called;

        public UsernamePasswordLoginServletWrapper(String username,
                                                   String password) {
            this.expectedUsername = username;
            this.expectedPassword = password;
            this.called = false;
        }

        protected void authenticateUser(HttpServletRequest request,
                                        String username,
                                        String password) throws LoginException {
            Assert.assertEquals(expectedUsername, username);
            Assert.assertEquals(expectedPassword, password);
            this.called = true;

            throw new RuntimeException("test completed");
        }

        public boolean isCalled() {
            return called;
        }
    }

}
