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
        this(message, false);
    }

    public StorageException(String message, boolean retry) {
        super(message);
        this.retry = retry;
    }

    public StorageException(String message, Throwable throwable) {
        this(message, throwable, false);
    }

    public StorageException(String message, Throwable throwable,
                            boolean retry) {
        super(message, throwable);
        this.retry = retry;
    }

    public StorageException(Throwable throwable) {
        super(throwable);
    }

    public boolean isRetry() {
        return retry;
    }

}
