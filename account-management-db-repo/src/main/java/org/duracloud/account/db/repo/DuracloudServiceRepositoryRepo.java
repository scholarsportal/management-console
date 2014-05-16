/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.repo;

import org.duracloud.account.db.model.ServicePlan;
import org.duracloud.account.db.model.ServiceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Erik Paulsson
 *         Date: 7/9/13
 */
@Repository(value="serviceRepositoryRepo")
public interface DuracloudServiceRepositoryRepo extends JpaRepository<ServiceRepository, Long> {

    /**
     * This method returns the set of service repositories matching the
     * given version.
     *
     * @param version of the service repository
     * @param servicePlan of the service repository
     * @return matching service repositories
     */
    public ServiceRepository findByVersionAndServicePlan(String version,
                                                  ServicePlan servicePlan);
}
