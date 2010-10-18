/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.annotation;

import org.duracloud.account.annotation.UniqueSubdomainValidator;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.mock.AccountManagerServiceImpl;
import org.duracloud.annotation.MockConstraintValidatorContext;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class UniqueSubdomainTest {
	
	@Test
	public void test(){
		AccountManagerService ams = new AccountManagerServiceImpl();
		UniqueSubdomainValidator v = new UniqueSubdomainValidator();
		v.setAccountManagerService(ams);
		Assert.assertTrue(v.isValid("testdomain", new MockConstraintValidatorContext()));
	}
	
}
