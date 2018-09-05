/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import static org.junit.Assert.assertTrue;

import com.thoughtworks.selenium.Selenium;
import org.duracloud.account.app.AMATestConfig;

/**
 * @author Daniel Bernstein
 */
public class UserHelper {
    public static final String TEST_EMAIL = AMATestConfig.getTestEmail();

    private UserHelper() {
        // Ensures no instances are made of this class, as there are only static members.
    }

    public static void createUser(Selenium sc, String username, String password, String passwordConfirm, String first,
                                  String last, String email, String question, String answer) {
        UrlHelper.openRelative(sc, "/users/new");
        sc.type("first-name-text", first);
        sc.type("last-name-text", last);
        sc.type("email-text", email);
        sc.type("username-text", username);
        sc.type("password-text", password);
        sc.type("password-confirm-text", passwordConfirm);
        sc.type("securityQuestion-text", question);
        sc.type("securityAnswer-text", answer);
        SeleniumHelper.clickAndWait(sc, "create-user-button");
    }

    public static void confirmNewUserFormIsLoaded(Selenium sc) {
        assertTrue(sc.isElementPresent("id=new-user-form"));
    }

    public static String createAndConfirm(Selenium sc) {
        return createUser(sc);
    }

    public static String createUser(Selenium sc) {
        String username = "t" + System.currentTimeMillis();
        String password = generatePassword(username);
        createUser(sc,
                   username,
                   password,
                   password,
                   username,
                   "Testman",
                   TEST_EMAIL,
                   "question",
                   "answer");
        return username;
    }

    public static void openUserProfile(Selenium sc) {
        UrlHelper.openRelative(sc, "/users/profile");
    }

    public static void openUserProfile(Selenium sc, String username) {
        UrlHelper.openRelative(sc, "/users/byid/" + username);
    }

    protected static String formatUserURL(String username, String suffix) {
        return UrlHelper.formatURL("/users/byid/" + username + (suffix != null ? suffix : ""));
    }

    public static String[] createDefaultUserFormParams(String username,
                                                       String password,
                                                       String passwordConfirm) {
        return new String[] {
            username, password, passwordConfirm, username, "Testerman",
            UserHelper.TEST_EMAIL, "question", "answer"};

    }

    public static String[] createDefaultUserFormParams() {
        return createDefaultUserFormParams(generateUsername());
    }

    public static String generateUsername() {
        return "t" + System.currentTimeMillis();
    }

    public static String[] createDefaultUserFormParams(String username) {
        String password = generatePassword(username);
        return createDefaultUserFormParams(username, password, password);
    }

    static String generatePassword(String username) {
        return "pw-" + username;
    }

}
