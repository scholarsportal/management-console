/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradav.servlet.methods;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

abstract class Helper {

    private static final String RFC_1123_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    public static long getLength(HttpServletRequest req) {
        try {
            String string = req.getHeader("Content-Length");
            if (string == null) return -1;
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            return -1;
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
