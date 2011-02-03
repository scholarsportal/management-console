/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudRepoMgr;
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

    public DuracloudInstanceServiceImpl(int accountId,
                                        DuracloudInstance instance,
                                        DuracloudRepoMgr repoMgr) {
        this.accountId = accountId;
        this.instance = instance;
        this.repoMgr = repoMgr;
    }

    @Override
    public DuracloudInstance getInstanceInfo() {
        // Default method body
        return null;
    }

    @Override
    public InstanceState getState() {
        // Default method body
        return null;
    }

    @Override
    public void stop() {
        // Default method body
    }

    @Override
    public void restart() {
        // Default method body
    }

    @Override
    public void setUserRoles(Set<DuracloudUser> users) {
        // Default method body
    }
    
}
