/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;

/**
 * @author: Bill Branan
 * Date: Feb 3, 2011
 */
public class DuracloudInstanceManagerServiceImpl implements DuracloudInstanceManagerService {

    private DuracloudRepoMgr repoMgr;

    public DuracloudInstanceManagerServiceImpl(DuracloudRepoMgr repoMgr) {
        this.repoMgr = repoMgr;
    }

    @Override
    public DuracloudInstanceService createInstance(int accountId, int imageId) {
        // Default method body
        return null;
    }

    @Override
    public DuracloudInstanceService getInstanceService(String instanceId)
        throws DuracloudInstanceNotAvailableException {
        // Default method body
        return null;
    }
}
