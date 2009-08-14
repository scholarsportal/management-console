package org.duracloud.storage.domain;

/**
 * Exception thrown by StorageProvider implementations.
 *
 * @author Bill Branan
 */
public class StorageException extends Exception {

    private static final long serialVersionUID = 1L;

    public StorageException (String message) {
        super(message);
    }

    public StorageException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public StorageException(Throwable throwable) {
        super(throwable);
    }

}
