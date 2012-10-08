/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.Selenium;

/**
 * @author Daniel Bernstein
 *         Date: Feb 13, 2012
 * 
 */
public abstract class AbstractBaseTestForm<T extends FormBot>
    extends AbstractIntegrationTest {
    private Class<T> clazz = null;
    private T fb = null;
    AbstractBaseTestForm(Class<T> clazz){
        this.clazz = clazz;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.duracloud.account.app.integration.AbstractIntegrationTest#before()
     */
    @Override
    @Before
    public void before() throws Exception {
        super.before();
        fb = createFormBot();
        fb.login();
        openHome();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.duracloud.account.app.integration.AbstractIntegrationTest#after()
     */
    @Override
    @After
    public void after() {
        super.after();
    }

    @Test
    public void testOpen() {
        fb.open();
    }

    @Test
    public void testSubmit() {
        fb.open();
        fb.fill();
        fb.submit();
        Assert.assertTrue(fb.isSubmitSuccess());
        fb.delete();
    }
    
    @Test
    public void testCancel() {
        fb.open();
        fb.fill();
        fb.cancel();
        Assert.assertTrue(fb.isCancelSuccess());
    }

    public T createFormBot(){
        try {
            T formBot =  clazz.getConstructor(Selenium.class).newInstance(sc);
            return formBot;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    @Test
    public void testValidation() {
        List<Map<String,String>> invalidData = fb.createInvalidTestFieldData();
        for (Map<String,String> map : invalidData) {
            fb = createFormBot(map);
            fb.open();
            fb.fill();
            fb.submit();
            Assert.assertFalse(fb.isSubmitSuccess());
        }
    }

    private T createFormBot(Map<String, String> map) {
        T bot = createFormBot();
        bot.setTestFieldData(map);     
        return bot;
    }
}
