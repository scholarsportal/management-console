/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.instance.util;

import org.duracloud.account.monitor.instance.domain.InstanceInfo;

/**
 * This interface defines the contract for a utility to query the DuraCloud
 * instance of a given storage account.
 *
 * @author Andrew Woods
 *         Date: 7/15/11
 */
public interface InstanceUtil {

    /**
     * This method makes requests to the web applications hosted within the
     * target DuraCloud instance.
     *
     * @return instance info of results
     */
    public InstanceInfo pingWebApps();
}
