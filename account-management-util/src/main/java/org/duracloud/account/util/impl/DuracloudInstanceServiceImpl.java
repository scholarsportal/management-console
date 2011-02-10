/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.ProviderAccount;
import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.compute.DuracloudComputeProvider;
import org.duracloud.account.db.DuracloudProviderAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudInstanceService;

import java.util.Set;

/**
 * @author: Bill Branan
 * Date: Feb 3, 2011
 */
public class DuracloudInstanceServiceImpl implements DuracloudInstanceService {

    private int accountId;
    private DuracloudInstance instance;
    private DuracloudRepoMgr repoMgr;
    private ComputeProviderUtil computeProviderUtil;
    private DuracloudComputeProvider computeProvider;

    public DuracloudInstanceServiceImpl(int accountId,
                                        DuracloudInstance instance,
                                        DuracloudRepoMgr repoMgr,
                                        ComputeProviderUtil computeProviderUtil)
        throws DBNotFoundException {
        this(accountId, instance, repoMgr, computeProviderUtil, null);
    }

    protected DuracloudInstanceServiceImpl(int accountId,
                                           DuracloudInstance instance,
                                           DuracloudRepoMgr repoMgr,
                                           ComputeProviderUtil computeProviderUtil,
                                           DuracloudComputeProvider computeProvider)
        throws DBNotFoundException {
        this.accountId = accountId;
        this.instance = instance;
        this.repoMgr = repoMgr;
        this.computeProviderUtil = computeProviderUtil;

        if(null != computeProvider) {
            this.computeProvider = computeProvider;
        } else {
            initializeComputeProvider();
        }
    }

    private void initializeComputeProvider()
        throws DBNotFoundException {

        DuracloudProviderAccountRepo providerAcctRepo =
            repoMgr.getProviderAccountRepo();
        ProviderAccount computeProviderAcct =
            providerAcctRepo.findById(instance.getComputeProviderAccountId());

        this.computeProvider = computeProviderUtil
            .getComputeProvider(computeProviderAcct.getUsername(),
                                computeProviderAcct.getPassword());
    }

    @Override
    public DuracloudInstance getInstanceInfo() {
        return instance;
    }

    @Override
    public String getStatus() {
        return computeProvider.getStatus(instance.getProviderInstanceId());
    }

    @Override
    public void stop() {
        computeProvider.stop(instance.getProviderInstanceId());
    }

    @Override
    public void restart() {
        computeProvider.restart(instance.getProviderInstanceId());

        // TODO: Initialize instance
    }

    @Override
    public void setUserRoles(Set<DuracloudUser> users) {
        // Default method body
    }
    
}
