/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

public class UrlHelper {

    private UrlHelper() {
        // Ensures no instances are made of this class, as there are only static members.
    }

    public static String formatAccountId(Long accountId, String pattern) {
        return pattern.replace("{accountId}", String.valueOf(accountId));
    }

    public static String formatId(Long accountId, String pattern) {
        return pattern.replace("{id}", String.valueOf(accountId));
    }
}
