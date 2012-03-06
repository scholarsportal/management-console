package org.duracloud.account.util;

public class UrlHelper {
    public static String formatAccountId(int accountId, String pattern){
        return pattern.replace("{accountId}", String.valueOf(accountId));
    }

    public static String formatId(int accountId, String pattern) {
        return pattern.replace("{id}", String.valueOf(accountId));
    }
}
