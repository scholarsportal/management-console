/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.util;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudServerDetailsRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.error.DuracloudServerDetailsNotAvailableException;
import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: 2/9/12
 */
public class AccountUtil {

    public static ServerDetails getServerDetails(DuracloudRepoMgr repoMgr,
                                                 AccountInfo account) {
        return new AccountUtil().doGetServerDetails(repoMgr, account);
    }

    private ServerDetails doGetServerDetails(DuracloudRepoMgr repoMgr,
                                             AccountInfo account) {
        int serverDetailsId = account.getServerDetailsId();
        if(serverDetailsId < 0) {
            String err = "No ServerDetails are associated with account with ID" +
                         account.getId();
            throw new DuraCloudRuntimeException(err);
        }

        try {
            DuracloudServerDetailsRepo repo = repoMgr.getServerDetailsRepo();
            return repo.findById(serverDetailsId);
        } catch(DBNotFoundException e) {
            throw new DuracloudServerDetailsNotAvailableException(
                serverDetailsId);
        }
    }

}
