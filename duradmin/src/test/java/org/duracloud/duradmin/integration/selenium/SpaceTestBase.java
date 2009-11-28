
package org.duracloud.duradmin.integration.selenium;



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

}
