/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.annotation;

import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.duracloud.account.util.DuracloudUserService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class UsernameValidatorTest {
	
	@Test
	public void test() throws Exception{
		DuracloudUserService service = EasyMock.createMock(DuracloudUserService.class);
		service.checkUsername("buddha");
		EasyMock.expectLastCall();
        service.checkUsername("admin");
        EasyMock.expectLastCall().andThrow(new UserAlreadyExistsException("admin"));
		EasyMock.replay(service);
		UsernameValidator v = new UsernameValidator();
		v.setUserService(service);
		Assert.assertTrue(v.isValid("buddha", null));
		Assert.assertFalse(v.isValid("admin", null));
	}
	
}
