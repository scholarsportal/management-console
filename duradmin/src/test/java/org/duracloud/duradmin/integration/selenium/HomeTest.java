package org.duracloud.duradmin.integration.selenium;
import com.thoughtworks.selenium.SeleneseTestCase;

public class HomeTest extends SeleneseTestCase {
	public void setUp() throws Exception {
		setUp("http://localhost:8080/duradmin/", "*firefox");
	}
	public void testHome() throws Exception {
	    selenium.open("http://localhost:8080/duradmin/");
		assertTrue(selenium.isTextPresent("Home"));
	    assertTrue(selenium.isElementPresent("storageProviderId"));
	}
}
