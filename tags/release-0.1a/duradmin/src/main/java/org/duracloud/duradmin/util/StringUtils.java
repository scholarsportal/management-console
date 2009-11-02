package org.duracloud.duradmin.util;


public class StringUtils {
    public static boolean isEmptyOrAllWhiteSpace(String string){
        return string == null || string.trim().equals("");
    }
}
