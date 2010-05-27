package org.duracloud.storage.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: May 20, 2010
 */
public class TaskException extends DuraCloudRuntimeException {

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
