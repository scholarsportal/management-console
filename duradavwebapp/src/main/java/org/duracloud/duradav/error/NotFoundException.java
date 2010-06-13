/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradav.error;

import javax.servlet.http.HttpServletResponse;

import org.duracloud.duradav.core.Path;

/**
 * HTTP 404 (Not Found).
 */
public class NotFoundException
        extends WebdavException {

    public static final String STATUS_LINE = "HTTP/1.1 404 Not Found";

    private static final long serialVersionUID = 1L;

    public NotFoundException(Path path) {
        super(path, HttpServletResponse.SC_NOT_FOUND);
    }
}
