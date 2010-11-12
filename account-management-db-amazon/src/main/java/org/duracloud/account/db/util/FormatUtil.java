/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
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
