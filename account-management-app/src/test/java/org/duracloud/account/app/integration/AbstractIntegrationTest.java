/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.Assert;
import org.duracloud.account.app.AMATestConfig;
import org.duracloud.account.db.model.util.InitUserCredential;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public abstract class AbstractIntegrationTest {


    protected static Logger log =
        LoggerFactory.getLogger(AbstractIntegrationTest.class);

    protected Selenium sc;
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
        String url = createAppUrl();
        sc = createSeleniumClient(url);
        log.info("started selenium client on " + url);
    }
    
    protected String createAppUrl() throws Exception{
        return  "http://localhost:" + getPort() + getAppRoot() + "/";
    }
    

    /**
	 * 
	 */
    protected void logout() {
        sc.close();
        try {
            sc = createSeleniumClient(createAppUrl());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void login(String username, String password) {
        logout();
        LoginHelper.login(sc, username, password);
    }




    @After
    public void after() {
        sc.stop();
        sc = null;
        log.info("stopped selenium client");
    }

    protected boolean isTextPresent(String pattern) {
        return sc.isTextPresent(pattern);
    }

    protected boolean isElementPresent(String locator) {
        return sc.isElementPresent(locator);
    }

    protected Selenium createSeleniumClient(String url) {
        Selenium s =  new DefaultSelenium("localhost", 4444, "*pifirefox", url);
        s.start("addCustomRequestHeader=true");
        s.setTimeout(SeleniumHelper.DEFAULT_PAGE_LOAD_WAIT_IN_MS);
        return s;
    }

    protected void openUserProfilePage() {
        UserHelper.openUserProfile(sc);
    }
    

    protected void openAccountHome(String accountId) {
        open(formatAccountURL(accountId, null));
    }

    protected String formatURL(String path) {
        return UrlHelper.formatURL(path);
    }

    protected String formatAccountURL(String accountId, String suffix) {
        return getAppRoot()
            + "/accounts/byid/" + accountId + (suffix != null ? suffix : "");
    }

    protected void openAccountDetails(String accountId) {
        open(formatAccountURL(accountId, "/details/"));
    }

    protected void openHome(){
        UrlHelper.open(sc, SeleniumHelper.getAppRoot());
    }

    protected void open(String location){
        UrlHelper.open(sc, location);
    }

    protected void waitForPage() {
        SeleniumHelper.waitForPage(sc);
    }
    
    protected void clickAndWait(String locator) {
        SeleniumHelper.clickAndWait(sc, locator);
    }
    
    protected String createNewUser() {
        return UserHelper.createAndConfirm(sc);
    }

    private void deleteUser(Selenium s, String username) {
        new RootBot(s).deleteUser(username);
    }

    protected void deleteUserWithSeparateBrowser(String username) {
        Selenium newSc = null;

        try {
            newSc = createSeleniumClient(createAppUrl());
            deleteUser(newSc,username);
        } catch (Exception e) {
            log.error("failed to delete " + username + ": " + e.getMessage(), e);
            e.printStackTrace();
        }finally{
            if(newSc != null){
                newSc.close();
            }
        }
    }

    protected void deleteUser(String username) {
        deleteUser(sc, username);
    }

    protected void openUserProfile() {
        UserHelper.openUserProfile(sc);
    }
    
    protected void confirmGlobalErrorsPresent() {
        Assert.assertTrue(isElementPresent("css=.global-errors"));
    }

}
