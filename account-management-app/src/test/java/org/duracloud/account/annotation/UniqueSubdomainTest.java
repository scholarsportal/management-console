/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.annotation;

import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.annotation.MockConstraintValidatorContext;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class UniqueSubdomainTest {
	private AccountManagerService ams = null;
	private UniqueSubdomainValidator v = null;
	
	@Before
	public void before() throws DBConcurrentUpdateException{
		this.ams = EasyMock.createMock(AccountManagerService.class);
		EasyMock.expect(this.ams.checkSubdomain("opendomain")).andReturn(true);
		EasyMock.expect(this.ams.checkSubdomain("inusedomain")).andReturn(false);
		EasyMock.replay(this.ams);
		this.v = new UniqueSubdomainValidator();
	}

	@Test
	public void test() throws Exception {
		v.setAccountManagerService(ams);
		Assert.assertTrue(v.isValid("opendomain", new MockConstraintValidatorContext()));
		Assert.assertFalse(v.isValid("inusedomain", new MockConstraintValidatorContext()));

	}
	
}
