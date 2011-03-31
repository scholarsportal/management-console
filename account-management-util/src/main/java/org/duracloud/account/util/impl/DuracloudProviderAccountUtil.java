/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Bill Branan
 * Date: 3/28/11
 */
public class DuracloudProviderAccountUtil {

    private Logger log =
        LoggerFactory.getLogger(DuracloudProviderAccountUtil.class);

    private DuracloudRepoMgr repoMgr;

    public DuracloudProviderAccountUtil(DuracloudRepoMgr repoMgr) {
        this.repoMgr = repoMgr;
    }    

    public int createEmptyComputeProviderAccount() {
        int providerId = getIdUtil().newComputeProviderAccountId();

        ComputeProviderAccount providerAccount =
            new ComputeProviderAccount(providerId,
                                       ComputeProviderType.AMAZON_EC2,
                                       ComputeProviderAccount.PLACEHOLDER_VALUE,
                                       ComputeProviderAccount.PLACEHOLDER_VALUE,
                                       ComputeProviderAccount.PLACEHOLDER_VALUE,
                                       ComputeProviderAccount.PLACEHOLDER_VALUE,
                                       ComputeProviderAccount.PLACEHOLDER_VALUE);

        DuracloudComputeProviderAccountRepo repo =
            repoMgr.getComputeProviderAccountRepo();
        try {
            repo.save(providerAccount);
        } catch(DBConcurrentUpdateException e) {
            log.error("Error encountered attempting to create a compute " +
                      "provider account: " + e.getMessage());
            providerId = -1;
        }
        return providerId;
    }

    public int createEmptyStorageProviderAccount(StorageProviderType type) {
        int providerId = getIdUtil().newStorageProviderAccountId();

        StorageProviderAccount providerAccount =
            new StorageProviderAccount(providerId,
                                       type,
                                       StorageProviderAccount.PLACEHOLDER_VALUE,
                                       StorageProviderAccount.PLACEHOLDER_VALUE,
                                       false);

        DuracloudStorageProviderAccountRepo repo =
            repoMgr.getStorageProviderAccountRepo();
        try {
            repo.save(providerAccount);
        } catch(DBConcurrentUpdateException e) {
            log.error("Error encountered attempting to create a storage " +
                      "provider account: " + e.getMessage());
            providerId = -1;
        }
        return providerId;
    }

    private IdUtil getIdUtil() {
        return repoMgr.getIdUtil();
    }

}
