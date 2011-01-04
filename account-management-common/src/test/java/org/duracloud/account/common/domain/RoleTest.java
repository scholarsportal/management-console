/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author: Bill Branan
 * Date: Jan 3, 2011
 */
public class RoleTest {

    @Test
    public void testRoles() {
        Assert.assertTrue(Role.ROLE_ROOT.getRightsLevel() >
                          Role.ROLE_OWNER.getRightsLevel());
        Assert.assertTrue(Role.ROLE_OWNER.getRightsLevel() >
                          Role.ROLE_ADMIN.getRightsLevel());
        Assert.assertTrue(Role.ROLE_ADMIN.getRightsLevel() >
                          Role.ROLE_USER.getRightsLevel());

        Assert.assertNotSame(Role.ROLE_ROOT.getDisplayName(),
                             Role.ROLE_OWNER.getDisplayName());
        Assert.assertNotSame(Role.ROLE_OWNER.getDisplayName(),
                             Role.ROLE_ADMIN.getDisplayName());
        Assert.assertNotSame(Role.ROLE_ADMIN.getDisplayName(),
                             Role.ROLE_USER.getDisplayName());        
    }
}
