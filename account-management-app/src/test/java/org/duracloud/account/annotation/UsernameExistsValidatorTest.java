/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.annotation;

import org.duracloud.account.db.util.DuracloudUserService;
import org.duracloud.account.db.util.error.UserAlreadyExistsException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Daniel Bernstein
 *         Date: Feb 8, 2012
 */

public class UsernameExistsValidatorTest {

    @Test
    public void test() throws Exception {
        DuracloudUserService service =
            EasyMock.createMock(DuracloudUserService.class);
        service.checkUsername("buddha");
        EasyMock.expectLastCall().andThrow(new UserAlreadyExistsException("test"));
        service.checkUsername("admin");
        EasyMock.expectLastCall();
        EasyMock.replay(service);
        UsernameExistsValidator v = new UsernameExistsValidator();
        v.setUserService(service);
        Assert.assertTrue(v.isValid("buddha", null));
        Assert.assertFalse(v.isValid("admin", null));
        EasyMock.verify(service);
    }

}
