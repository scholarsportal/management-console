package org.duracloud.common.error;

/**
 * This class is the top-level Checked DuraCloud exception from which other
 * internal exceptions extend.
 *
 * @author Andrew Woods
 *         Date: Oct 24, 2009
 */
public class DuraCloudCheckedException extends Exception implements MessageFormattable {
    
    private MessageFormattable core;

    public DuraCloudCheckedException() {
        core = new DuraCloudExceptionCore();
    }

    public DuraCloudCheckedException(String message) {
        core = new DuraCloudExceptionCore(message);
    }

    public DuraCloudCheckedException(String message, String key) {
        core = new DuraCloudExceptionCore(message, key);
    }

    public DuraCloudCheckedException(String message, Throwable throwable) {
        core = new DuraCloudExceptionCore(message, throwable);
    }

    public DuraCloudCheckedException(String message,
                                     Throwable throwable,
                                     String key) {
        core = new DuraCloudExceptionCore(message, throwable, key);
    }

    public DuraCloudCheckedException(Throwable throwable) {
        core = new DuraCloudExceptionCore(throwable);
    }

    public DuraCloudCheckedException(Throwable throwable, String key) {
        core = new DuraCloudExceptionCore(throwable, key);
    }

    public String getKey() {
        return core.getKey();
    }

    public String[] getArgs() {
        return core.getArgs();
    }

    public void setArgs(String... args)
    {
        core.setArgs(args);
    }

    public String getFormattedMessage() {
        return core.getFormattedMessage();
    }
}
