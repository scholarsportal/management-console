/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.app.controller.HomeController;
import org.junit.Test;
import static org.junit.Assert.*;

public class HomeControllerTest {
	@Test
	public void test() throws Exception {
		HomeController c = new HomeController();
		assertNotNull(c.home());
	}

}
