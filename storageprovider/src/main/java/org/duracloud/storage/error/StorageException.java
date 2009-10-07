package org.duracloud.storage.error;

/**
 * Exception thrown by StorageProvider implementations.
 *
 * @author Bill Branan
 */
public class StorageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected boolean retry = true;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public StorageException(Throwable throwable) {
        super(throwable);
    }

    public boolean isRetry() {
        return retry;
    }

}
