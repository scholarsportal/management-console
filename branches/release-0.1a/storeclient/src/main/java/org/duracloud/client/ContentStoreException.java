package org.duracloud.client;

/**
 * Exception thrown by the ContentStore.
 *
 * @author Bill Branan
 */
public class ContentStoreException extends Exception {

    private static final long serialVersionUID = 1L;

    public ContentStoreException (String message) {
        super(message);
    }

    public ContentStoreException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ContentStoreException(Throwable throwable) {
        super(throwable);
    }

}
