/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import com.thoughtworks.selenium.Selenium;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 14, 2012
 *
 */
public abstract class SimpleFormBot extends BaseBot implements FormBot{
    private FormConfig config;
    public SimpleFormBot(Selenium sc){
         super(sc);
         config = createConfig();
    }
    
    protected FormConfig getConfig(){
        return this.config;
    }
    
    protected abstract FormConfig createConfig();

    public void open(){
        open(config.getFormPath());
        Assert.assertTrue(isOpen(config));
    }

    protected boolean isOpen(FormConfig config){
        return isElementPresent("id="+config.getFormId());
    }

    
    public void fill(){
        Map<String,String> params = config.getFieldNameValueMap();
        for(Map.Entry<String,String> entry : params.entrySet()){
            sc.type("id="+entry.getKey(), entry.getValue());
        }
    }

    public void submit(){
        clickAndWait("css=#"+config.getFormId()+ " button[type='submit']");
       
    }
    
    public void cancel(){
        clickAndWait("css=#cancel-button");
        Assert.assertTrue(isCancelSuccess(config));
    }
    
    private boolean isCancelSuccess(FormConfig config) {
        return isElementPresent(config.getCancelSuccessLocator());
    }

    protected boolean isSubmitSuccess(FormConfig config){
        return (!hasErrors() && isElementPresent(config.getSubmitSuccessLocator()));
    }

    private boolean hasErrors() {
        return isElementPresent("css=.global-errors");
    }

    public boolean isSubmitSuccess() {
        return isSubmitSuccess(config);
    }

    public boolean isCancelSuccess() {
        return isCancelSuccess(config);
    }
    
    @Override
    public Map<String, String> createTestFieldData() {
        return this.config.createDefaultFieldMap();
    }
    
    @Override
    public List<Map<String,String>> createInvalidTestFieldData() {
        List<Map<String, String>>  list = new LinkedList<Map<String, String>>();
        Map<String, String> fieldMap = createTestFieldData();
        removeOptionalFields(fieldMap);
        for (String key : fieldMap.keySet()) {
            Map<String, String> newMap = new HashMap<String, String>(fieldMap);
            newMap.put(key, "");
            list.add(newMap);
        }
        return list;
    }
    
    
    private void removeOptionalFields(Map<String, String> fieldMap) {
        List<String> optionalFields = getOptionalFields();
        if(optionalFields != null && optionalFields.size() > 0){
            for (String key : optionalFields) {
                fieldMap.remove(key);
            }
        }
    }

    protected List<String> getOptionalFields(){
        return new LinkedList<String>();
    }

    
    @Override
    public void setTestFieldData(Map<String, String> testData) {
        this.config.setFieldNameValueMap(testData);
    }
    
    @Override
    public Map<String, String> getTestFieldData() {
       return this.config.getFieldNameValueMap();
    }
    
    @Override
    public void delete() {
        String locator = this.config.getDeleteLocator();
        sc.click(locator);
        Assert.assertTrue(sc.isConfirmationPresent());
        sc.getConfirmation();
        SeleniumHelper.waitForPage(sc);
    }
}
