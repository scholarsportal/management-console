/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;

import java.util.HashSet;
import java.util.Set;

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
        // Get AccountInfo for this account
        // Get credentials for compute provider from ProviderAccount pointed
        //   to by AccountInfo (?) - needs to be added to AccountInfo
        // Create a ComputeProvider using credentials, call start(imageId)
        // Attach elastic IP (?) - should be done in ComputeProvider.start()
        // Set sub-domain to point to elastic IP (?)
        // Collect info to initialize instance
        //   - storage provider account IDs (?) - where do these get input?
        //   - service repository IDs (?) - how to know which ones to use?
        //   - root username/password (?) - where is this stored?
        // Initialize instance
        // Create DuracloudInstance object, store data in InstanceRepo
        return null;
    }

    @Override
    public DuracloudInstanceService getInstanceService(int accountId,
                                                       int instanceId)
        throws DuracloudInstanceNotAvailableException {

        DuracloudInstanceRepo instanceRepo = repoMgr.getInstanceRepo();
        try {
            DuracloudInstance instance = instanceRepo.findById(instanceId);
            return new DuracloudInstanceServiceImpl(accountId, instance, repoMgr);
        } catch(DBNotFoundException e) {
            throw new DuracloudInstanceNotAvailableException(e.getMessage(), e);
        }
    }

    @Override
    public Set<DuracloudInstanceService> getInstanceServices(int accountId)
        throws DuracloudInstanceNotAvailableException {

        DuracloudInstanceRepo instanceRepo = repoMgr.getInstanceRepo();
        Set<Integer> instanceIds = instanceRepo.getIds();
        Set<DuracloudInstanceService> instanceServices =
            new HashSet<DuracloudInstanceService>();

        for(int instanceId : instanceIds) {
            instanceServices.add(getInstanceService(accountId, instanceId));
        }
        return instanceServices;
    }
}
