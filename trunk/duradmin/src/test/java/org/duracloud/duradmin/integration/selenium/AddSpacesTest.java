
package org.duracloud.duradmin.integration.selenium;

import java.util.Date;


public class AddSpacesTest
        extends SpacesHomeTest{

    private String spaceId = "selenium-unit-test-space-" + new Date().getTime();
    
    public void setUp() throws Exception {
        super.setUp();
        navigateToSpacesPage();
    }
    
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    private void removeSpace(String spaceId) throws Exception{
        navigateToSpacesPage();
        selenium.mouseOver("//tr[@id='"+ spaceId + "']");
        selenium.click("//tr[@id='"+ spaceId + "']//a[@id='removeSpaceLink']");
        assertTrue(selenium.isConfirmationPresent());
        selenium.getConfirmation();
        selenium.waitForPageToLoad("10000");
        navigateToSpacesPage();
        assertFalse(selenium.isElementPresent("//tr[@id='" + spaceId + "']"));
    }
    
    @Override
    public void testSpaces() throws Exception {
    }
    
    public void testAddAndRemoveSpace() throws Exception {
        clickAndWait("//a[@id='addSpaceLink']");
        assertTrue(selenium.isElementPresent("//label[@id='spaceIdLabel']"));
        selenium.type("//input[@id='spaceId']", spaceId);
        clickAndWait("//input[@id='addSpaceButton']");
        navigateToSpacesPage();
        assertTrue(selenium.isElementPresent("//tr[@id='" + spaceId + "']"));
        removeSpace(spaceId);

    }
}
