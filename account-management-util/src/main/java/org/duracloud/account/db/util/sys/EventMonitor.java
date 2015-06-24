/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.sys;

import org.duracloud.account.db.model.util.AccountCreationInfo;

/**
 * @author Andrew Woods
 *         Date: 3/21/11
 */
public interface EventMonitor {

    /**
     * This method defines the contract for receiving notification of the
     * 'account-creation' event.
     *
     * @param accountCreationInfo of new account
     */
    public void accountCreated(AccountCreationInfo accountCreationInfo);
}
