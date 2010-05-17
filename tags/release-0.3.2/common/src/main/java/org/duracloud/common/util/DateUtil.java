
package org.duracloud.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

public class DateUtil {

    private static final String DEFAULT_PATTERN =
            "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";

    public static Date convertToDate(String text, String pattern)
            throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);

        Date date = format.parse(text);
        return date;
    }

    public static Date convertToDate(String text) throws ParseException {
        return convertToDate(text, DEFAULT_PATTERN);
    }

}
