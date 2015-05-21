/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import java.util.List;
import java.util.Map;


/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 14, 2012
 *
 */
public interface FormBot {
    public void login();
    public void open();

    public void fill();

    public void submit();
    
    public void cancel();
    
    public boolean isSubmitSuccess();

    public boolean isCancelSuccess();
    
    public Map<String,String> createTestFieldData();
    
    public List<Map<String,String>> createInvalidTestFieldData();
    
    public void setTestFieldData(Map<String,String> testData);

    public Map<String, String> getTestFieldData();
    
    public void delete();
}
