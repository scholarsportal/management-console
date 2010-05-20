package org.duracloud.storage.error;

/**
 * @author: Bill Branan
 * Date: May 20, 2010
 */
public class UnsupportedTaskException extends TaskException {

    public UnsupportedTaskException(String task) {
        super(task + " is not a supported task");
    }

}