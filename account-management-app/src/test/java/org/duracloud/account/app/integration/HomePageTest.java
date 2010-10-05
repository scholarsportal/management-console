/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
/**
 * 
 *
 */
public class HomePageTest extends AbstractIntegrationTest {
	@Test
	public void test() throws Exception {
		sc.open(getAppRoot());
		assertTrue(this.isTextPresent("Welcome"));
	}
}
