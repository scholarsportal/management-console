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

    private DuracloudRepoMgr repoMgr;

    public AccountUtil(DuracloudRepoMgr repoMgr) {
        this.repoMgr = repoMgr;
    }

    /**
     *  Retrieves the server details for a given account.
     *
     * @param account from which server details are requested
     * @return ServerDetails associated with the account
     * @throws DuraCloudRuntimeException if no server details are associated
     *                                   with this account
     * @throws DuracloudServerDetailsNotAvailableException if the server details
     *     associated with this account are not found in the database
     */
    public ServerDetails getServerDetails(AccountInfo account) {
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
