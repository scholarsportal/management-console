/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.duracloud.account.app.controller.ServiceRepositoryController;

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
    public static final String HOST_NAME_FIELD_KEY = "hostName";

    @Override
    protected Map<String, String> createDefaultFieldMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(HOST_NAME_FIELD_KEY, "testhost.duracloud.org");
        map.put("spaceId", "test-space-id");
        map.put("xmlId", "test-service-xml");
        map.put("version", "test-version");
        map.put("userName", "test-username");
        map.put("password", "test-password");
        return map;
    }

    @Override
    public String getFormPath() {
        return ServiceRepositoryController.BASE_MAPPING
            + ServiceRepositoryController.NEW_MAPPING;
    }

    @Override
    public String getFormId() {
        return "form";
    }

    @Override
    String getCancelSuccessLocator() {
        return "css=table#serviceRepositories";
    }

    @Override
    public String getSubmitSuccessLocator() {
        return "css=.success";
    }
    
    private String formatHostNameLocator(String hostName) {
        String template = "css=table#serviceRepositories [data-{0}=\"{1}\"]";
        String locator = MessageFormat.format(template, "hostName", hostName);
        return locator;
    }

    @Override
    public String getDeleteLocator() {
        String host =
            getFieldNameValueMap().get(HOST_NAME_FIELD_KEY);
        return formatHostNameLocator(host) + " .delete";
    }

}
