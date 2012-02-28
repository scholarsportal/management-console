/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.duracloud.account.app.controller.AccountClusterController;

import com.thoughtworks.selenium.Selenium;

/**
 * @author Daniel Bernstein Date: Feb 15, 2012
 * 
 */
public class TestAccountClusterForm
    extends AbstractBaseTestForm<AccountClusterFormBot> {
    public TestAccountClusterForm() {
        super(AccountClusterFormBot.class);
    }
}

class AccountClusterFormBot extends SimpleFormBot {
    public AccountClusterFormBot(Selenium sc) {
        super(sc);
    }

    @Override
    public void login() {
        loginRoot();
    }

    @Override
    protected FormConfig createConfig() {
        return new AccountClusterFormConfig();
    }

}

class AccountClusterFormConfig extends FormConfig {

    @Override
    protected Map<String, String> createDefaultFieldMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", "cluster-name");
        return map;
    }

    @Override
    public String getFormPath() {
        return AccountClusterController.BASE_MAPPING+AccountClusterController.NEW_MAPPING;
    }

    @Override
    public String getFormId() {
        return "form";
    }

    @Override
    String getCancelSuccessLocator() {
        return "css=table#clusters";
    }

    @Override
    public String getSubmitSuccessLocator() {
        return "css=.success";
    }

    private String formatNameLocator(String name) {
        String template = "css=#clusters [data-{0}=\"{1}\"]";
        String locator =
            MessageFormat.format(template, "name", name);
        return locator;
    }

    @Override
    public String getDeleteLocator() {
        String name =
            getFieldNameValueMap().get("name");
        return formatNameLocator(name) + " .delete";
    }

}
