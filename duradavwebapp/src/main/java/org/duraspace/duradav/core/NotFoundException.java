package org.duraspace.duradav.core;

import javax.servlet.http.HttpServletResponse;

/**
 * HTTP 404 (Not Found).
 */
public class NotFoundException
        extends WebdavException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(Path path) {
        super(path, HttpServletResponse.SC_NOT_FOUND);
    }
}
