package org.duracloud.common.web;

/**
 * Exception thrown by REST Resources.
 *
 * @author Bill Branan
 */
public class RestResourceException extends Exception {

    private static final long serialVersionUID = 1L;

    public RestResourceException (String message) {
        super(message);
    }

    public RestResourceException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
