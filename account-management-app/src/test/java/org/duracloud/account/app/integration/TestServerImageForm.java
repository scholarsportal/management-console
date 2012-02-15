/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.duracloud.account.app.controller.ManageServerImagesController;

import com.thoughtworks.selenium.Selenium;

/**
 * @author Daniel Bernstein
 *         Date: Feb 15, 2012
 * 
 */
public class TestServerImageForm extends AbstractBaseTestForm<ServerImageFormBot> {
    public TestServerImageForm() {
        super(ServerImageFormBot.class);
    }
}

class ServerImageFormBot extends SimpleFormBot {
    public ServerImageFormBot(Selenium sc) {
        super(sc);
    }

    @Override
    protected List<String> getOptionalFields() {
        List<String> optionalFields = super.getOptionalFields();
        optionalFields.add(ServerImageFormConfig.DESCRIPTION_FIELD_KEY);
        return optionalFields;
    }
    
    @Override
    public void login() {
        loginRoot();
    }
    
    @Override
    protected FormConfig createConfig() {
        return new ServerImageFormConfig();
    }

}

class ServerImageFormConfig extends FormConfig {
    public static final String DESCRIPTION_FIELD_KEY = "description-text";
    public static final String PROVIDER_IMAGE_FIELD_KEY = "provider-image-id-text";

    @Override
    protected Map<String, String> createDefaultFieldMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(PROVIDER_IMAGE_FIELD_KEY, "ami-99999999");
        map.put(DESCRIPTION_FIELD_KEY, "This is a description.");
        map.put("version-text", "test-version");
        map.put("password-text", "test-password");
        return map;
    }
    
   

    @Override
    public String getFormPath() {
        return ManageServerImagesController.MANAGE_SERVER_IMAGES_NEW_MAPPING;
    }

    @Override
    public String getFormId() {
        return "serverImageForm";
    }

    @Override
    String getCancelSuccessLocator() {
        return "css=.server-images";
    }

    @Override
    public String getSubmitSuccessLocator() {
        return formatProviderImageIdLocator(getFieldNameValueMap().get(PROVIDER_IMAGE_FIELD_KEY));
    }

    private String formatProviderImageIdLocator(String providerImageId) {
        String template = "css=.server-images [data-{0}=\"{1}\"]";
        String locator = MessageFormat.format(template, "provider-image-id", providerImageId);
        return locator;
    }

    @Override
    public String getDeleteLocator() {
        String providerImageId =
            getFieldNameValueMap().get(PROVIDER_IMAGE_FIELD_KEY);
        return formatProviderImageIdLocator(providerImageId) + " .delete";
    }

}
