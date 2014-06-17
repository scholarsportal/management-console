/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.sys;

import org.duracloud.account.db.model.DuracloudUser;
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
     * @param user     who created new account
     */
    public void accountCreated(AccountCreationInfo accountCreationInfo,
                               DuracloudUser user);
}
