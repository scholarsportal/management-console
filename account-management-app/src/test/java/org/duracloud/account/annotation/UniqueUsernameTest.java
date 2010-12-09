/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.annotation;

import org.duracloud.account.util.DuracloudUserService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class UniqueUsernameTest {
	
	@Test
	public void test() throws Exception{
		DuracloudUserService service = EasyMock.createMock(DuracloudUserService.class);
		EasyMock.expect(service.isUsernameAvailable("buddha")).andReturn(true);
		EasyMock.expect(service.isUsernameAvailable("admin")).andReturn(false);
		EasyMock.replay(service);
		
		UniqueUsernameValidator v = new UniqueUsernameValidator();
		v.setUserService(service);
		Assert.assertTrue(v.isValid("buddha", null));
		Assert.assertFalse(v.isValid("admin", null));
	}
	
}
