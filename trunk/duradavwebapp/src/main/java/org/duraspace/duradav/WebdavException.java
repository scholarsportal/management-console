package org.duraspace.duradav;

public class WebdavException
        extends Exception {

    private static final long serialVersionUID = 1L;

    public WebdavException(String message) {
        super(message);
    }

    public WebdavException(String message, Throwable cause) {
        super(message, cause);
    }

}
