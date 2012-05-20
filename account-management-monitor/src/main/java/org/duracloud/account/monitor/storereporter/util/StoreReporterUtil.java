/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
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
