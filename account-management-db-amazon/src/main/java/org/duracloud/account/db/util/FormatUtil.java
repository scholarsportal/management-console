/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util;

import com.amazonaws.services.simpledb.util.SimpleDBUtils;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class FormatUtil {

    public static String padded(Integer counter) {
        return SimpleDBUtils.encodeZeroPadding(counter, 10);
    }
}
