/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.instance.util;

import org.duracloud.account.db.model.AccountInfo;

/**
 * This interface defines the contract of a factory that creates instances of
 * InstanceUtil.
 *
 * @author Andrew Woods
 *         Date: 7/15/11
 */
public interface InstanceUtilFactory {

    /**
     * This method creates or returns a cached instance of a utility object
     * for interacting with DuraCloud instances.
     *
     * @param acct to which the instance belongs
     * @return instance utility
     */
    public InstanceUtil getInstanceUtil(AccountInfo acct);
}
