package org.duraspace.duradav.error;

import javax.servlet.http.HttpServletResponse;

import org.duraspace.duradav.core.Path;

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
