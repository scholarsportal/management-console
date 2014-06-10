/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.hadoop.util;

import org.duracloud.account.monitor.hadoop.util.HadoopUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 7/12/11
 */
public class HadoopUtilSTATETest {

    @Test
    public void testFromString() {
        String[] runningText = new String[]{"STARTING",
                                            "BOOTSTRAPPING",
                                            "RUNNING",
                                            "WAITING"};
        String[] completedText = new String[]{"SHUTTING_DOWN",
                                              "COMPLETED",
                                              "TERMINATED",
                                              "FAILED"};

        String[] junkText = new String[]{"", "JUNK", null};

        for (String text : runningText) {
            verifyFromString(text, HadoopUtil.STATE.RUNNING);
        }

        for (String text : completedText) {
            verifyFromString(text, HadoopUtil.STATE.COMPLETED);
        }

        for (String text : junkText) {
            verifyFromString(text, HadoopUtil.STATE.UNKNOWN);
        }
    }

    private void verifyFromString(String text, HadoopUtil.STATE expected) {

        HadoopUtil.STATE state = HadoopUtil.STATE.fromString(text);
        Assert.assertNotNull(state);
        Assert.assertEquals(expected, state);
    }
}
