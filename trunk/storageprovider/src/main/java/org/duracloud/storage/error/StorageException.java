package org.duracloud.storage.error;

/**
 * Exception thrown by StorageProvider implementations.
 *
 * @author Bill Branan
 */
public class StorageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public static final boolean RETRY = true;
    public static final boolean NO_RETRY = false;

    protected boolean retry;

    public StorageException(String message) {
        this(message, NO_RETRY);
    }

    public StorageException(String message, boolean retry) {
        super(message);
        this.retry = retry;
    }

    public StorageException(String message, Throwable throwable) {
        this(message, throwable, NO_RETRY);
    }

    public StorageException(String message, Throwable throwable, boolean retry) {
        super(message, throwable);
        this.retry = retry;
    }

    public StorageException(Throwable throwable) {
        this(throwable, NO_RETRY);
    }

    public StorageException(Throwable throwable, boolean retry) {
        super(throwable);
        this.retry = retry;
    }

    public boolean isRetry() {
        return retry;
    }

}
