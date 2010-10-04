package org.duracloud.account.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for all controllers.
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public abstract class AbstractController {
	
	@SuppressWarnings("unused")
	protected Logger log = LoggerFactory.getLogger(getClass());


	public void init(){
		
	}
	
	public void destry(){
		
	}
}
