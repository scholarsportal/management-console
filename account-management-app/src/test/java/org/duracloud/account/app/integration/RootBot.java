/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

import junit.framework.Assert;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class RootBot extends AdminBot {
    private Logger log = LoggerFactory.getLogger(RootBot.class);
    public RootBot(Selenium sc, RootUserCredential rootUserCredential) {
        super(sc,
              rootUserCredential.getUsername(),
              rootUserCredential.getPassword());
    }

    public RootBot(Selenium sc){
        this(sc, new RootUserCredential());
    }
    
    public void deleteAllTestUsers(){
        //make sure root is logged in.
        login();

        //open root manage page
        openRootUserList();

        while(true){
            try{
                String username = sc.getAttribute("css=[data-username^='t13']@data-username");
                if(StringUtils.isBlank(username)){
                    return;
                }
                
                clickDelete(username);
                
            }catch(SeleniumException x){
                x.printStackTrace();
                return;
            }
        }
    }
    
    public void deleteUser(String username){
        log.info("starting deletion user " + username);
        logout();
        log.info("logged out");

        //make sure root is logged in.
        login();

        openRootUserList();
        clickDelete(username);
        //log out
        LoginHelper.logout(sc);
        log.info("done deletion user " + username);

    }

    private void openRootUserList() {
        open("/root/users");
    }

    private void clickDelete(String username) {
        String deleteUserSelector =
            "css=#users [data-username='" + username + "'] button.delete";
        
        //verify that the element in question is present
        Assert.assertTrue(sc.isElementPresent(deleteUserSelector));
        sc.click(deleteUserSelector);
        Assert.assertTrue(sc.isConfirmationPresent());
        log.info("confirmation=" + sc.getConfirmation());
        SeleniumHelper.waitForPage(sc);
        
        //verify that it's gone
        Assert.assertFalse(sc.isElementPresent(deleteUserSelector));
    }

    public void clickRootConsoleLink() {
        SeleniumHelper.clickAndWait(sc, "id=root-console-link");
    }

    public void confirmManageUsersPageIsLoaded() {
        Assert.assertTrue(sc.isElementPresent("id=users"));
    }
}
