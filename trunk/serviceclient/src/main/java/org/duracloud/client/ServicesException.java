package org.duracloud.client;

/**
 * Exception thrown by the Services Manager.
 *
 * @author Bill Branan
 */
public class ServicesException extends Exception {

    private static final long serialVersionUID = 1L;

    public ServicesException (String message) {
        super(message);
    }

    public ServicesException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ServicesException(Throwable throwable) {
        super(throwable);
    }

}
