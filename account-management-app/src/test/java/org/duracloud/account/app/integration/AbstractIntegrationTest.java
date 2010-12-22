/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import org.duracloud.account.app.AMATestConfig;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.DefaultSelenium;

public abstract class AbstractIntegrationTest {
    protected Logger log = LoggerFactory.getLogger(getClass());

    protected DefaultSelenium sc;
    private String port;
    private static String DEFAULT_PORT = "9000";
    protected String getAppRoot() {
    	return SeleniumHelper.getAppRoot();
    }

    private String getPort() throws Exception {
        if (null == port) {
            port = AMATestConfig.getPort();

            try {
                Integer.parseInt(port);

            } catch (NumberFormatException e) {
                port = DEFAULT_PORT;
            }
        }
        return port;
    }

    @Before
    public void before() throws Exception {
        String url = "http://localhost:" + getPort() + getAppRoot()+"/";
        sc = createSeleniumClient(url);
        sc.start();
        log.info("started selenium client on " + url);
    }

    /**
	 * 
	 */
	protected void logout() {
		LoginHelper.logout(sc);
	}

	
	protected void login(String username, String password){
		LoginHelper.login(sc, username, password);
	}
	
	protected void loginAdmin(){
		login("admin","admin");
	}
	
	@After
    public void after() {
        sc.stop();
        sc = null;
        log.info("stopped selenium client");
    }

    /**
     * sc.isTextPresent is not working properly -
     * selenium is reporting back that the body empty
     * assertTrue(sc.isTextPresent("Welcome"));
     *
     * @param text
     * @return
     */
    protected boolean isTextPresent(String text) {
        return sc.getHtmlSource().contains(text);

    }

    protected DefaultSelenium createSeleniumClient(String url) {
        return new DefaultSelenium("localhost", 4444, "*firefox", url);
	}

	protected void openUserProfilePage(String username){
		sc.open(getAppRoot()+"/users/byid/" + username);
	}
	
	/**
	 * @param accountId
	 */
	protected void openAccountHome(String accountId) {
		sc.open(getAppRoot()+"/accounts/byid/"+accountId);
		SeleniumHelper.waitForPage(sc);
		log.debug("after opening accountHome: " + sc.getHtmlSource());
	}

	protected void openAccountDetails(String accountId) {
		sc.open(getAppRoot()+"/accounts/byid/"+accountId +"/details/");
		SeleniumHelper.waitForPage(sc);
		log.debug("after opening accountHome: " + sc.getHtmlSource());
	}

	protected void waitForPage() {
		SeleniumHelper.waitForPage(sc);
	}

}