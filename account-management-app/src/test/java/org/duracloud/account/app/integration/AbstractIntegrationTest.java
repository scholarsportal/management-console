/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.DefaultSelenium;

public abstract class AbstractIntegrationTest {
	protected Logger log = LoggerFactory.getLogger(getClass());

	protected DefaultSelenium sc;
	
	protected String getAppRoot() {
		return "/ama";
	}
	@Before

	public void before() {
		String url = "http://localhost:9000" + getAppRoot();
		sc = createSeleniumClient(url);
		sc.start();
		log.info("started selenium client on " + url);
	}
	
	@After
	public void after() {
		sc.stop();
		sc = null;
		log.info("stopped selenium client");
	}

	/**
	 * 	sc.isTextPresent is not working properly - 
	 *	selenium is reporting back that the body empty
	 *	assertTrue(sc.isTextPresent("Welcome"));
     *
	 * @param text
	 * @return
	 */
	protected boolean isTextPresent(String text){
		return sc.getHtmlSource().contains(text);
		
	}

	protected DefaultSelenium createSeleniumClient(String url) {
		return new DefaultSelenium("localhost", 4444, "*firefox", url);
	}

}
