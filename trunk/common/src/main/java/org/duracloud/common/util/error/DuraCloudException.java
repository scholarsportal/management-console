package org.duracloud.common.util.error;

import java.text.MessageFormat;

/**
 * This class is the top-level DuraCloud exception from which other internal
 *  exceptions extend.
 *
 * @author Andrew Woods
 *         Date: Oct 24, 2009
 */
public class DuraCloudException extends Exception {

    private String key = "duracloud.error.general";

    private String[] args;

    public DuraCloudException() {
        super();
    }

    public DuraCloudException(String message) {
        super(message);
    }

    public DuraCloudException(String message, String key) {
        super(message);
        this.key = key;
    }

    public DuraCloudException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DuraCloudException(String message, Throwable throwable, String key) {
        super(message, throwable);
        this.key = key;
    }

    public DuraCloudException(Throwable throwable) {
        super(throwable);
    }

    public DuraCloudException(Throwable throwable, String key) {
        super(throwable);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String[] getArgs() {
        if (args == null) {
            args = new String[0];
        }
        return args;
    }

    protected void setArgs(String... args) {
        this.args = args;
    }

    public String getFormatedMessage() {
        String pattern = null;
        try {
            pattern = ExceptionMessages.getMessagePattern(getKey());
        } catch (Exception e) {
            // do nothing
        }
        
        if (pattern == null) {
            return this.getMessage();
        } else {
            return MessageFormat.format(pattern, getArgs());
        }
    }
}
