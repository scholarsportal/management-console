/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
@Controller
public class AccessDeniedController {
	@RequestMapping("/access-denied")
	public String get(){
		return "access-denied";
	}
}
