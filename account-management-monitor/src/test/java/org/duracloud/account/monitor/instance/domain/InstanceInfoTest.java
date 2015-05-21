/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.instance.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 7/17/11
 */
public class InstanceInfoTest {

    private InstanceInfo instance;

    private String subdomain = "subdomain";
    private String contextPath = "context/path/";

    @Before
    public void setUp() throws Exception {
        instance = new InstanceInfo(subdomain);
    }

    @Test
    public void testHasErrorsA() throws Exception {
        Assert.assertFalse(instance.hasErrors());
    }

    @Test
    public void testHasErrorsB() throws Exception {
        String error = "error message";
        instance.setError(contextPath, error);
        Assert.assertTrue(instance.hasErrors());
    }

    @Test
    public void testHasErrorsC() throws Exception {
        String error = "error message";
        instance.setServerStatus(error);
        Assert.assertTrue(instance.hasErrors());
    }

    @Test
    public void testSetSuccess() throws Exception {
        String path0 = contextPath + "0";
        String path1 = contextPath + "1";

        verifyContains(path0, false);
        verifyContains(subdomain, true);
        instance.setSuccess(path0);
        Assert.assertFalse(instance.hasErrors());

        verifyContains(path0, false);
        verifyContains(subdomain, true);

        String error = "error message";
        instance.setError(path1, error);

        verifyContains(path0 + " (OK)", true);
        verifyContains(path1 + " (" + error + ")", true);
    }

    private void verifyContains(String path, boolean expected) {
        String text = instance.toString();
        Assert.assertNotNull(text);
        Assert.assertEquals(text, expected, text.contains(path));
    }

}
