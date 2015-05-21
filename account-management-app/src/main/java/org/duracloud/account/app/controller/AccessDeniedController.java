/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
