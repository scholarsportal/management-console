/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.storereporter.util;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.common.model.Credential;

/**
 * This interface defines the contract of a factory that creates instances of
 * StoreReporterUtil.
 *
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public interface StoreReporterUtilFactory {

    /**
     * This method creates or returns a cached store-reporter of a utility
     * object for interacting with DuraCloud instances.
     *
     * @param acct       to which the instance belongs
     * @param credential for accessing arg acct
     * @return store-reporter utility
     */
    public StoreReporterUtil getStoreReporterUtil(AccountInfo acct,
                                                  Credential credential);
}
