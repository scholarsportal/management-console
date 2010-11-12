/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all controllers.
 *
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 */

public abstract class AbstractController {

    protected Logger log = LoggerFactory.getLogger(getClass());

	protected static final String PREFIX = "";


	public static final String NEW_MAPPING = "/new";

    public void init() {
    	log.info("initializing " + this.toString());
    }

    public void destroy() {
    	log.info("destroying " + this.toString());
    }
}
