package org.duraspace.duradav;

/**
 * Signals a webdav error.
 */
public class WebdavException
        extends Exception {

    private static final long serialVersionUID = 1L;

    private final int statusCode;

    /**
     * Creates an instance with a message and http status code.
     *
     * @param message the message
     * @param statusCode the status code
     */
    public WebdavException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Creates an instance with a message, http status code, and cause.
     *
     * @param message the message
     * @param statusCode the status code
     * @param cause the underlying cause of this exception
     */
    public WebdavException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * Gets the http status code for this exception.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }
}
