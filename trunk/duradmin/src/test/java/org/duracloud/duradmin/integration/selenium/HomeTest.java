
package org.duracloud.duradmin.integration.selenium;


public class HomeTest
        extends SeleniumTestBase{

    public void setUp() throws Exception {
        setUp("http://localhost:8080/duradmin/", "*firefox");
    }

    public void testHome() throws Exception {
        goHome();
        assertTrue(selenium.isTextPresent("Welcome"));
        assertTrue(selenium.isElementPresent("storageProviderId"));
    }
}
