package org.duraspace.duradav.servlet.methods;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

abstract class Helper {

    private static final String RFC_1123_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    public static int getDepth(HttpServletRequest req, int defaultValue) {
        String depth = req.getHeader("Depth");
        if (depth == null) {
            return defaultValue;
        } else if (depth.equals("0")) {
            return 0;
        } else if (depth.equals("1")) {
            return 1;
        } else if (depth.equals("infinity")) {
            return -1;
        } else {
            throw new RuntimeException("Unrecognized Depth: " + depth);
        }
    }

    /**
     * Formats the given date according to RFC 1123, in GMT.
     * For example, <code>Mon, 02 Mar 2009 10:23:36 GMT</code>.
     */
    public static String formatDate(Date date) {
        DateFormat formatter = new SimpleDateFormat(RFC_1123_FORMAT,
                                                    Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);
    }

}
