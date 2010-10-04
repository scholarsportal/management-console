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
