/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import com.thoughtworks.selenium.Selenium;
/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class BaseBot {
    protected final Selenium sc;
    public BaseBot(Selenium sc){
        this.sc = sc;
    }
    
    protected void open(String path) {
        UrlHelper.openRelative(sc,path);
    }

    public void logout(){
        LoginHelper.logout(sc);
    }

    public void loginRoot(){
        LoginHelper.loginRoot(sc);
    }

    public void clickCancelButton(){
        clickBackButton();
    }
    public void clickBackButton(){
        SeleniumHelper.clickAndWait(sc, "id=cancel-button");
    }
    
    protected void clickAndWait(String locator) {
        SeleniumHelper.clickAndWait(sc, locator);
    }

    protected boolean isElementPresent(String locator) {
        return sc.isElementPresent(locator);
    }
    
}
