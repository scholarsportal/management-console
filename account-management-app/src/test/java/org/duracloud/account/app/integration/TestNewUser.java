/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class TestNewUser extends AbstractIntegrationTest {

    @Override
    public void before() throws Exception {
        super.before();
    }

    @Test
    public void testNewUserLink() throws Exception {
        openHome();
        String newUserLink = "id=new-user-link";
        assertTrue(isElementPresent(newUserLink));
        clickAndWait(newUserLink);
        confirmNewUserFormIsLoaded();
    }

    private void confirmNewUserFormIsLoaded() {
        UserHelper.confirmNewUserFormIsLoaded(sc);
    }

    @Test
    public void newUserFormSuccess() {
        String username = UserHelper.createUser(sc);
        Assert.assertTrue(isTextPresent(username));
        Assert.assertTrue(sc.isElementPresent("id=account-list"));
        logout();
        deleteUser(username);
    }

    @Test
    public void testNullValuesCheck() {
        String[] userFormParams = UserHelper.createDefaultUserFormParams();
        for (int i = 0; i < userFormParams.length; i++) {
            String value = userFormParams[i];
            userFormParams[i] = "";
            createUser(userFormParams);
            confirmNewUserFormIsLoaded();
            confirmGlobalErrorsPresent();
            userFormParams[i] = value;
        }
    }

    @Test
    public void testPasswordNoMatch() {
        String[] userFormParams =
            UserHelper.createDefaultUserFormParams("t"
                                                       + System.currentTimeMillis(),
                                                   "password",
                                                   "password-nomatch");
        createUser(userFormParams);
        confirmNewUserFormIsLoaded();
        confirmGlobalErrorsPresent();
    }
    
    private void createUser(String[] userFormParams) {
        UserHelper.createUser(sc,
                              userFormParams[0],
                              userFormParams[1],
                              userFormParams[2],
                              userFormParams[3],
                              userFormParams[4],
                              userFormParams[5],
                              userFormParams[6],
                              userFormParams[7]);
    }



    protected void confirmGlobalErrorsPresent() {
        Assert.assertTrue(isElementPresent("css=.global-errors"));
    }

}
