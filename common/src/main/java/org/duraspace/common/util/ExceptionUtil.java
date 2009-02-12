package org.duraspace.common.util;


public class ExceptionUtil {

    public static String getStackTraceAsString(Exception e)
    {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement elem : e.getStackTrace())
        {
            sb.append(elem.toString() + "\n");
        }
        return sb.toString();
    }

}
