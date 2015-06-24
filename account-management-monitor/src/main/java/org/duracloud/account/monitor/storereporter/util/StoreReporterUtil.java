/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.storereporter.util;

import org.duracloud.account.monitor.storereporter.domain.StoreReporterInfo;

/**
 * This interface defines the contract for a utility to query the DuraCloud
 * instance store-reporter of a given storage account.
 *
 * @author Andrew Woods
 *         Date: 5/17/12
 */
public interface StoreReporterUtil {

    /**
     * This method makes requests to the store-reporter hosted within the
     * target DuraCloud instance.
     *
     * @return store-reporter info of results
     */
    public StoreReporterInfo pingStorageReporter();
}
