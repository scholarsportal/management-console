package org.duracloud.client;

/**
 * Exception thrown by the ContentStore.
 *
 * @author Bill Branan
 */
public class ContentStoreException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for ContentStoreException.</p>
     *
     * @param message the exception message
     */
    public ContentStoreException (String message) {
        super(message);
    }

    /**
     * <p>Constructor for ContentStoreException.</p>
     *
     * @param message the exception message
     * @param throwable the underlying exception
     */
    public ContentStoreException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * <p>Constructor for ContentStoreException.</p>
     *
     * @param throwable the underlying exception
     */
    public ContentStoreException(Throwable throwable) {
        super(throwable);
    }

}
