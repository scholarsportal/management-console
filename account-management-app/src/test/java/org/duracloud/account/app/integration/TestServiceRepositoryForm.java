/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.duracloud.account.app.controller.ManageServiceReposController;

import com.thoughtworks.selenium.Selenium;

/**
 * @author Daniel Bernstein
 *         Date: Feb 14, 2012
 * 
 */
public class TestServiceRepositoryForm extends AbstractBaseTestForm<ServiceRepositoryFormBot> {
    public TestServiceRepositoryForm() {
        super(ServiceRepositoryFormBot.class);
    }
}

class ServiceRepositoryFormBot extends SimpleFormBot {
    public ServiceRepositoryFormBot(Selenium sc) {
        super(sc);
    }
    
    @Override
    public void login() {
        loginRoot();
    }
    
    @Override
    protected FormConfig createConfig() {
        return new ServiceRepoAddFormConfig();
    }

}

class ServiceRepoAddFormConfig extends FormConfig {
    public static final String HOST_NAME_FIELD_KEY = "host-name-text";

    @Override
    protected Map<String, String> createDefaultFieldMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(HOST_NAME_FIELD_KEY, "testhost.duracloud.org");
        map.put("space-id-text", "test-space-id");
        map.put("xml-id-text", "test-service-xml");
        map.put("version-text", "test-version");
        map.put("username-text", "test-username");
        map.put("password-text", "test-password");
        return map;
    }

    @Override
    public String getFormPath() {
        return ManageServiceReposController.MANAGE_SERVICE_REPO_NEW_MAPPING;
    }

    @Override
    public String getFormId() {
        return "service-repo-form";
    }

    @Override
    String getCancelSuccessLocator() {
        return "css=.service-repos";
    }

    @Override
    public String getSubmitSuccessLocator() {
        return formatHostNameLocator(getFieldNameValueMap().get(HOST_NAME_FIELD_KEY));
    }

    private String formatHostNameLocator(String hostName) {
        String template = "css=.service-repos [data-{0}=\"{1}\"]";
        String locator = MessageFormat.format(template, "host-name", hostName);
        return locator;
    }

    @Override
    public String getDeleteLocator() {
        String host =
            getFieldNameValueMap().get(HOST_NAME_FIELD_KEY);
        return formatHostNameLocator(host) + " .delete";
    }

}
