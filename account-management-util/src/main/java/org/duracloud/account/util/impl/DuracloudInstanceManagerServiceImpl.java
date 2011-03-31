/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: Feb 3, 2011
 */
public class DuracloudInstanceManagerServiceImpl implements DuracloudInstanceManagerService {

	private Logger log =
        LoggerFactory.getLogger(DuracloudInstanceManagerServiceImpl.class);

    private DuracloudRepoMgr repoMgr;
    private ComputeProviderUtil computeUtil;

    public DuracloudInstanceManagerServiceImpl(DuracloudRepoMgr repoMgr,
                                               ComputeProviderUtil computeUtil) {
        this.repoMgr = repoMgr;
        this.computeUtil = computeUtil;
    }

    @Override
    public DuracloudInstanceService createInstance(int accountId, int imageId) {
        // Get AccountInfo for this account
        // Get credentials for compute provider from ComputeProviderAccount pointed
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
    public DuracloudInstanceService getInstanceService(int instanceId)
        throws DuracloudInstanceNotAvailableException {
        DuracloudInstanceRepo instanceRepo = repoMgr.getInstanceRepo();
        try {
            DuracloudInstance instance = instanceRepo.findById(instanceId);
            return new DuracloudInstanceServiceImpl(instance.getAccountId(),
                                                    instance,
                                                    repoMgr,
                                                    computeUtil);
        } catch(DBNotFoundException e) {
            throw new DuracloudInstanceNotAvailableException(e.getMessage(), e);
        }
    }

    @Override
    public Set<DuracloudInstanceService> getInstanceServices(int accountId) {

        Set<Integer> instanceIds = getInstanceIds(accountId);
        Set<DuracloudInstanceService> instanceServices =
            new HashSet<DuracloudInstanceService>();

        if(null != instanceIds) {
            for(int instanceId : instanceIds) {
                try {
                    instanceServices.add(getInstanceService(instanceId));
                } catch(DuracloudInstanceNotAvailableException e) {
                    log.error("The instance with ID: " + instanceId +
                              " was found to be associated with the " +
                              "account with ID: " +  accountId +
                              " but the instance could not be found!");
                }
            }
        }
        return instanceServices;
    }

    private Set<Integer> getInstanceIds(int accountId) {
        try {
            DuracloudInstanceRepo instanceRepo = repoMgr.getInstanceRepo();
            return instanceRepo.findByAccountId(accountId);
        } catch (DBNotFoundException e) {
            return new HashSet<Integer>();
        }
    }
}