
package org.duracloud.duradmin.integration.selenium;

import java.util.Date;



public class SpaceTestBase
        extends SeleniumTestBase{

    
    
    public void setUp() throws Exception {
        super.setUp();
        
    }
   
    protected void navigateToSpacesPage() throws Exception{
        goHome();
        clickAndWait("//a[@id='spacesMenuItem']");
        assertTrue(selenium.isTextPresent("Metadata"));
    }

    protected void removeSpace(String spaceId) throws Exception {
        navigateToSpacesPage();
        selenium.mouseOver("//tr[@id='"+ spaceId + "']");
        selenium.click("//tr[@id='"+ spaceId + "']//a[@id='removeSpaceLink']");
        assertTrue(selenium.isConfirmationPresent());
        selenium.getConfirmation();
        selenium.waitForPageToLoad("10000");
        navigateToSpacesPage();
        assertFalse(selenium.isElementPresent("//tr[@id='" + spaceId + "']"));
    }

    protected String addSpace() throws Exception {
        String spaceId = "test-space-selenium-" + new Date().getTime();
        clickAndWait("//a[@id='addSpaceLink']");
        assertTrue(selenium.isElementPresent("//label[@id='spaceIdLabel']"));
        selenium.type("//input[@id='spaceId']", spaceId);
        clickAndWait("//input[@id='addSpaceButton']");
        navigateToSpacesPage();
        assertTrue(selenium.isElementPresent("//tr[@id='" + spaceId + "']"));
        return spaceId;
    }

}
