/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.duracloud.account.app.controller.AccountClusterController;
import org.junit.Assert;

import com.thoughtworks.selenium.Selenium;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Mar 8, 2012
 * 
 */
public class AccountClusterFormBot extends SimpleFormBot {
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

    public String createCluster() {
        open();
        fill();
        submit();
        Assert.assertTrue(isSubmitSuccess());
        return AccountClusterFormConfig.TEST_CLUSTER_NAME;
    }

    public void deleteCluster(){
        open("/root/clusters");
        clickAndWait(getConfig().getDeleteLocator());
    }
}

class AccountClusterFormConfig extends FormConfig {

    static final String TEST_CLUSTER_NAME = "cluster-name";

    @Override
    protected Map<String, String> createDefaultFieldMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("name", TEST_CLUSTER_NAME);
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