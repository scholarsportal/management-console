/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
