package org.duracloud.common.error;

import java.text.MessageFormat;


/**
 * This class is the core utility for supporting DuraCloud exceptions and
 * user-friendly formatted messages.
 *
 * @author Andrew Woods
 *         Date: Nov 20, 2009
 */
public class DuraCloudExceptionCore extends Throwable implements MessageFormattable {

    private String key = "duracloud.error.general";
    private String[] args;

    public DuraCloudExceptionCore() {
        super();
    }

    public DuraCloudExceptionCore(String message) {
        super(message);
    }

    public DuraCloudExceptionCore(String message, String key) {
        super(message);
        this.key = key;
    }

    public DuraCloudExceptionCore(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DuraCloudExceptionCore(String message,
                                  Throwable throwable,
                                  String key) {
        super(message, throwable);
        this.key = key;
    }

    public DuraCloudExceptionCore(Throwable throwable) {
        super(throwable);
    }

    public DuraCloudExceptionCore(Throwable throwable, String key) {
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

    public void setArgs(String... args) {
        this.args = args;
    }

    public String getFormattedMessage() {
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
