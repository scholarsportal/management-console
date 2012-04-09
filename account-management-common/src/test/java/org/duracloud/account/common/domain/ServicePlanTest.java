/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 4/8/12
 */
public class ServicePlanTest {

    @Test
    public void testSupportsMedia() throws Exception {
        for (ServicePlan plan : ServicePlan.values()) {
            if (plan.equals(ServicePlan.STARTER_ARCHIVING)) {
                Assert.assertFalse(plan.supportsMedia());
            } else {
                Assert.assertTrue(plan.supportsMedia());
            }
        }
    }

}
