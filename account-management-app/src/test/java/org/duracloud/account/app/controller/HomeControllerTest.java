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
