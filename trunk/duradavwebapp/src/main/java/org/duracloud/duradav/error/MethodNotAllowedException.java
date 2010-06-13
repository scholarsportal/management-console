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
 * HTTP 405 (Method Not Allowed).
 */
public class MethodNotAllowedException
        extends WebdavException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance.
     *
     * @param path the path of the resource in question.
     */
    public MethodNotAllowedException(Path path) {
        super(path, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    /**
     * Creates an instance with details.
     *
     * @param path the path of the resource in question.
     * @param details additional info on the problem.
     */
    public MethodNotAllowedException(Path path, String details) {
        super(path, HttpServletResponse.SC_METHOD_NOT_ALLOWED, details);
    }
}
