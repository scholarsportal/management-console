package org.duracloud.services.webapputil.error;

import org.duracloud.services.common.error.ServiceRuntimeException;

/**
 * @author Andrew Woods
 *         Date: Nov 30, 2009
 */
public class WebAppDeployerException extends ServiceRuntimeException {

    public WebAppDeployerException(String msg) {
        super(msg);
    }

    public WebAppDeployerException(String msg, Throwable e) {
        super(msg, e);
    }

    public WebAppDeployerException(Exception e) {
        super(e);
    }
}
