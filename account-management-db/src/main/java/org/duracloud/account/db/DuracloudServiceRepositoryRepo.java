/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.db.error.DBNotFoundException;

/**
 * @author: Bill Branan
 * Date: Feb 2, 2011
 */
public interface DuracloudServiceRepositoryRepo  extends BaseRepo<ServiceRepository> {

    /**
     * This method returns the set of service repositories matching the
     * given version.
     *
     * @param version of the service repository
     * @param servicePlan of the service repository
     * @return matching service repositories
     * @throws org.duracloud.account.db.error.DBNotFoundException if no item found
     */
    public ServiceRepository findByVersionAndPlan(String version,
                                                  ServicePlan servicePlan)
        throws DBNotFoundException;

}
