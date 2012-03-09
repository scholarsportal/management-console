/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.flow.createaccount.CreateAccountFlowHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Mar 6, 2012
 * 
 */
public class TestNewAccountWizard extends AbstractIntegrationTest {

    private RootBot rootBot;
    @Override
    public void before() throws Exception {
        super.before();
        this.rootBot = new RootBot(sc);
    }

    @Test
    public void testCreateCommunityAccount() throws Exception {
        UrlHelper.openRelative(sc, "/"+CreateAccountFlowHandler.FLOW_ID);
        rootBot.login();
        assertNewAccountFormIsPresent();
        String accountName = "test"+System.currentTimeMillis();
        Map<String,String> fields = createNewAccountFormInput(accountName,AccountType.COMMUNITY);
        fillForm(fields);
        clickAndWait("id=next");
        
        assertInfoMessagePresent();
        assertAccountInList(accountName);
        deleteAccount(accountName);
    }

    private void assertNewAccountFormIsPresent() {
        Assert.assertTrue(isElementPresent("id=newAccountForm"));
    }

    private void fillForm(Map<String, String> fields) {
        for(Map.Entry<String,String> e : fields.entrySet()){
            if(e.getValue().startsWith("value=")){
                sc.select("id="+e.getKey(), e.getValue());
            }else{
                sc.type("id="+e.getKey(), e.getValue());
            }
        }
    }

    private void assertAccountInList(String accountName) {
        Assert.assertTrue(isElementPresent(formatAccountListSelector(accountName)));
    }

    private String formatAccountListSelector(String accountName) {
        return MessageFormat.format("css=[data-name=\"{0}\"]", accountName);
    }

    private void assertInfoMessagePresent() {
        Assert.assertTrue(isElementPresent("css=.message.info"));        
    }

    private Map<String, String>
        createNewAccountFormInput(String accountName, AccountType accountType) {
        Map<String,String> fields = new HashMap<String,String>();
        fields.put("acctName", accountName);
        fields.put("orgName", "test organization");
        fields.put("department", "test department");
        fields.put("subdomain", accountName);
        fields.put("accountType", "value=" + accountType);
        return fields;
    }

    private void deleteAccount(String accountName) {
        String accountListSelector = formatAccountListSelector(accountName);
        clickAndWait(accountListSelector+" .delete");
        assertInfoMessagePresent();
        Assert.assertTrue(!isElementPresent(accountListSelector));
    }


    @Test
    public void testCreateFullAccount() throws Exception {
        UrlHelper.openRelative(sc, "/"+CreateAccountFlowHandler.FLOW_ID);
        rootBot.login();
        assertNewAccountFormIsPresent();
        String accountName = "test"+System.currentTimeMillis();
        Map<String,String> fields = createNewAccountFormInput(accountName,AccountType.FULL);
        fillForm(fields);
        clickAndWait("id=next");
        Assert.assertTrue(isElementPresent("id=fullAccountForm"));
        clickAndWait("id=finish");
        assertInfoMessagePresent();
        assertAccountInList(accountName);
        deleteAccount(accountName);
    }

}