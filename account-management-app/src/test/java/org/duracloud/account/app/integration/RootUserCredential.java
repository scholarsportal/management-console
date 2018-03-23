/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

public class RootUserCredential {

    protected RootUserCredential() {
        // Ensures no instances are made of this class, as there are only static members.
    }

    public static String getUsername() {
        return "rpw";
    }

    public static String getPassword() {
        return "password";
    }
}
