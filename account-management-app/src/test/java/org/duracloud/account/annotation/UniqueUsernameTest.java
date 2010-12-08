/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.annotation;

import org.duracloud.account.db.mockimpl.MockDuracloudAccountRepo;
import org.duracloud.account.db.mockimpl.MockDuracloudUserRepo;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.impl.DuracloudUserServiceImpl;
import org.duracloud.annotation.MockConstraintValidatorContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class UniqueUsernameTest {
	
	@Test
	public void test() throws Exception{
        DuracloudUserService service = new DuracloudUserServiceImpl(new MockDuracloudUserRepo(),
                                                                    new MockDuracloudAccountRepo(),
                                                                    null,
                                                                    null);
				UniqueUsernameValidator v = new UniqueUsernameValidator();
		v.setUserService(service);
		Assert.assertTrue(v.isValid("buddha", new MockConstraintValidatorContext()));
		Assert.assertFalse(v.isValid("admin", new MockConstraintValidatorContext()));
	}
	
}
