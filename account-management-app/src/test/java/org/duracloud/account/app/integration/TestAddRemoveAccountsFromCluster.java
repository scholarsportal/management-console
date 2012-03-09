/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Mar 8, 2012
 * 
 */
public class TestAddRemoveAccountsFromCluster extends AbstractIntegrationTest {

    private RootBot rootBot;
    
    private AccountClusterFormBot formBot;
    private String addAccountButtonLocator = "css=#add-accounts";
    private String testCluster;
    
    @Override
    public void before() throws Exception {
        super.before();
        UrlHelper.openRelative(sc, "/");
        this.rootBot = new RootBot(sc);
        this.rootBot.login();
        formBot = new AccountClusterFormBot(sc);
        testCluster = formBot.createCluster();
        clickAndWait("css=[data-name='"+testCluster+"'] a[class='cluster-detail-link']");
        Assert.assertTrue(isElementPresent(addAccountButtonLocator));

    }

    @Test
    public void testAddAccountsToCluster(){
        clickAndWait(addAccountButtonLocator);
        String addSelectedButtonLocator = "css=#add-selected";
        Assert.assertTrue(isElementPresent("css=#accountSelectionForm"));
        //check the first account in the list.
        sc.click("css=#accountIds1");
        clickAndWait(addSelectedButtonLocator);
        Assert.assertTrue(isElementPresent("css=.message.info"));
    }

    @Test
    public void testRemoveAccountsFromCluster(){
        //add accounts to cluster.
        testAddAccountsToCluster();
        Assert.assertTrue(isElementPresent("css=#accountSelectionForm"));
        sc.click("css=#accountIds1");
        clickAndWait("css=#remove-selected");
        Assert.assertTrue(isElementPresent("css=.message.info"));
    }
    
    
    @Override
    public void after() {
        formBot.deleteCluster();
        super.after();
        
    }

}
