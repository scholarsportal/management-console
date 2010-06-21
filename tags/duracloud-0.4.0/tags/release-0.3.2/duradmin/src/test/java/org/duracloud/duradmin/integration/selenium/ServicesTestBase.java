
package org.duracloud.duradmin.integration.selenium;



public class ServicesTestBase
        extends SeleniumTestBase{

    
    
    public void setUp() throws Exception {
        super.setUp();
        
    }
   
    protected void navigateToServicesPage() throws Exception{
        goHome();
        clickAndWait("//a[@id='servicesMenuItem']");
        assertTrue(selenium.isTextPresent("Deployed"));
    }

}
