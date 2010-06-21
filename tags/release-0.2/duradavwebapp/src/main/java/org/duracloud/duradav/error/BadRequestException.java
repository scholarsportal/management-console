package org.duracloud.duradav.error;

import javax.servlet.http.HttpServletResponse;

import org.duracloud.duradav.core.Path;

/**
 * HTTP 400 (Bad Request).
 */
public class BadRequestException
        extends WebdavException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance.
     *
     * @param path the path of the resource in question.
     */
    public BadRequestException(Path path) {
        super(path, HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Creates an instance with details.
     *
     * @param path the path of the resource in question.
     * @param details additional info on the problem.
     */
    public BadRequestException(Path path, String details) {
        super(path, HttpServletResponse.SC_BAD_REQUEST, details);
    }
}
