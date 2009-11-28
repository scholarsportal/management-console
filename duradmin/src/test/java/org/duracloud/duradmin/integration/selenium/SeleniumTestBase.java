
package org.duracloud.duradmin.integration.selenium;

import com.thoughtworks.selenium.SeleneseTestCase;

public abstract class SeleniumTestBase
        extends SeleneseTestCase {

    public void setUp() throws Exception {
        setUp("http://localhost:8080/duradmin/", "*firefox");
    }

    protected String getBaseURL(){
     return "http://localhost:8080/duradmin/";   
    }
    
    protected void goHome() throws Exception {
        selenium.open(getBaseURL());
    }

    protected void clickAndWait(String pattern) {
        selenium.click(pattern);
        selenium.waitForPageToLoad("10000");
        
    }
}
