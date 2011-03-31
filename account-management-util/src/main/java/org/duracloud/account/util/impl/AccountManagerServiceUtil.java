/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for AccountManagerService.
 *
 * Created to get around a cyclic dependency involving
 * UserDetailsPropagator and DuracloudUserService
 *
 * @author: Bill Branan
 * Date: 3/17/11
 */
public class AccountManagerServiceUtil {

    private Logger log =
        LoggerFactory.getLogger(AccountManagerServiceUtil.class);

    private DuracloudRepoMgr repoMgr;

    public AccountManagerServiceUtil(DuracloudRepoMgr duracloudRepoMgr) {
        this.repoMgr = duracloudRepoMgr;
    }

    public AccountService getAccount(int accountId)
        throws AccountNotFoundException {
        try {
            AccountInfo acctInfo = repoMgr.getAccountRepo().findById(accountId);
            DuracloudProviderAccountUtil providerAccountUtil =
                new DuracloudProviderAccountUtil(repoMgr);
            return new AccountServiceImpl(acctInfo,
                                          repoMgr,
                                          providerAccountUtil);
        } catch (DBNotFoundException e) {
            throw new AccountNotFoundException(accountId);
        }
    }

}
