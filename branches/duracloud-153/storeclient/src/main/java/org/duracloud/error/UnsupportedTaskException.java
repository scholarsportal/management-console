package org.duracloud.error;

/**
 * @author: Bill Branan
 * Date: May 20, 2010
 */
public class UnsupportedTaskException extends ContentStoreException {

    private static final String messageKey = "duracloud.error.durastore.task";    

    public UnsupportedTaskException(String taskName, Throwable t) {
        super("Task " + taskName + "is not supported", t, messageKey);
        setArgs(taskName, t.getMessage());
    }

}
