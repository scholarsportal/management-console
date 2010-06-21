package org.duracloud.services.script.error;

import org.duracloud.services.common.error.ServiceRuntimeException;

/**
 * @author Bill Branan
 *         Date: Dec 11, 2009
 */
public class ScriptServiceException extends ServiceRuntimeException {

    public ScriptServiceException(String msg) {
        super(msg);
    }

    public ScriptServiceException(String msg, Throwable e) {
        super(msg, e);
    }

    public ScriptServiceException(Exception e) {
        super(e);
    }
}