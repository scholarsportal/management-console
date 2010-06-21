package org.duracloud.services.hellowebappwrapper.error;

import org.duracloud.services.common.error.ServiceRuntimeException;

/**
 * @author Andrew Woods
 *         Date: Dec 10, 2009
 */
public class WebappWrapperException extends ServiceRuntimeException {

    public WebappWrapperException(String msg) {
        super(msg);
    }

    public WebappWrapperException(String msg, Throwable e) {
        super(msg, e);
    }

    public WebappWrapperException(Exception e) {
        super(e);
    }
}