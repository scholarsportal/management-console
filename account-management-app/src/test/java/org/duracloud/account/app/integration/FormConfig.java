/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import java.util.Map;

/**
 * @author Daniel Bernstein
 *         Date: Feb 13, 2012
 * 
 */
public abstract class FormConfig {
    private Map<String,String> fieldNameValueMap = null;
    protected void setFieldNameValueMap(Map<String, String> fieldNameValueMap){
        this.fieldNameValueMap = fieldNameValueMap;
    }
    
    public Map<String, String> getFieldNameValueMap() {
        if(this.fieldNameValueMap == null){
            this.fieldNameValueMap = createDefaultFieldMap();
        }
        return this.fieldNameValueMap;
    }
    
    abstract protected Map<String,String> createDefaultFieldMap();

    abstract public String getFormPath();

    abstract public String getFormId();

    abstract String getCancelSuccessLocator();

    public String getSubmitSuccessLocator(){
            return "css=.info";
    }

    abstract public String getDeleteLocator();

}
