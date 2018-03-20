/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class TestNewUser extends AbstractIntegrationTest {

    @Override
    public void before() throws Exception {
        super.before();
    }

    private void confirmNewUserFormIsLoaded() {
        UserHelper.confirmNewUserFormIsLoaded(sc);
    }

    @Test
    public void newUserFormSuccess() throws InterruptedException {
        String username = UserHelper.createUser(sc);
        Assert.assertTrue(sc.isTextPresent("Success"));
        deleteUserWithSeparateBrowser(username);
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
            UserHelper.createDefaultUserFormParams("t" + System.currentTimeMillis(),
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

}
