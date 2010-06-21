package org.duracloud.duraservice.domain;

/**
 * Exception thrown for service errors.
 *
 * @author Bill Branan
 */
public class ServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public ServiceException (String message) {
        super(message);
    }

    public ServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ServiceException(Throwable throwable) {
        super(throwable);
    }

}
