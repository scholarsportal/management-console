/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author Andrew Woods
 *         Date: 7/12/11
 */
public class HadoopMonitorException extends DuraCloudCheckedException {

    public HadoopMonitorException(String msg, Throwable e) {
        super(msg, e);
    }
}
