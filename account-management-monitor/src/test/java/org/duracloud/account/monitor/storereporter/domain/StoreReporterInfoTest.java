/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.storereporter.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public class StoreReporterInfoTest {

    private StoreReporterInfo info;

    private String subdomain = "subdomain";

    @Before
    public void setUp() throws Exception {
        info = new StoreReporterInfo(subdomain);
    }

    @Test
    public void testHasErrorsA() throws Exception {
        Assert.assertFalse(info.hasErrors());
    }

    @Test
    public void testHasErrorsB() throws Exception {
        String error = "error message";
        info.setError(error);
        Assert.assertTrue(info.hasErrors());
    }

    @Test
    public void testSetSuccess() throws Exception {
        String prefix = "https://" + subdomain + ".duracloud.org status: ";
        String error = "error message";
        String textError = prefix + "FAILURE\n" + error;
        String textOK = prefix + "OK";

        verifyContains(textOK, true);

        info.setSuccess();
        Assert.assertFalse(info.hasErrors());
        verifyContains(textOK, true);

        info.setError(error);
        Assert.assertTrue(info.hasErrors());
        verifyContains(textError, true);
        verifyContains(textOK, false);
    }

    private void verifyContains(String path, boolean expected) {
        String text = info.toString();
        Assert.assertNotNull(text);
        Assert.assertEquals(text + " | " + path, expected, text.equals(path));
    }

}
