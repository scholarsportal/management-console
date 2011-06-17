/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Bill Branan
 * Date: Feb 2, 2011
 */
public class TestDuracloudServiceRepositoryRepoImpl extends BaseTestDuracloudRepoImpl {

    private DuracloudServiceRepositoryRepoImpl serviceRepositoryRepo;

    private static final String DOMAIN = "TEST_DURACLOUD_SERVICE_REPOSITORIES";

    private static final String hostName = "hostName";
    private static final String spaceId = "spaceId";
    private static final String username = "username";
    private static final String password = "password";

    private static final ServiceRepository.ServiceRepositoryType verifiedType =
        ServiceRepository.ServiceRepositoryType.VERIFIED;
    private static final ServiceRepository.ServiceRepositoryType privateType =
        ServiceRepository.ServiceRepositoryType.PRIVATE;

    @Before
    public void setUp() throws Exception {
        serviceRepositoryRepo = createServiceRepositoryRepo();
    }

    private static DuracloudServiceRepositoryRepoImpl createServiceRepositoryRepo()
        throws Exception {
        return new DuracloudServiceRepositoryRepoImpl(getDBManager(), DOMAIN);
    }

    @After
    public void tearDown() throws Exception {
        for(Integer itemId : serviceRepositoryRepo.getItemIds()) {
            serviceRepositoryRepo.delete(itemId);
        }
        verifyRepoSize(serviceRepositoryRepo, 0);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        createServiceRepositoryRepo().removeDomain();
    }

    @Test
    public void testNotFound() {
        boolean thrown = false;
        try {
            serviceRepositoryRepo.findById(-100);
            Assert.fail("exception expected");

        } catch (DBNotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetIds() throws Exception {
        String version1 = "1.0";
        String version2 = "2.0";

        ServicePlan servicePlan0 = ServicePlan.PROFESSIONAL;
        ServicePlan servicePlan1 = ServicePlan.STARTER_ARCHIVING;
        ServicePlan servicePlan2 = ServicePlan.STARTER_MEDIA;

        ServiceRepository serviceRepo0 =
            createServiceRepo(0, verifiedType, servicePlan0, version1);
        ServiceRepository serviceRepo1 =
            createServiceRepo(1, verifiedType, servicePlan1, version2);
        ServiceRepository serviceRepo2 =
            createServiceRepo(2, privateType, servicePlan2, version2);

        serviceRepositoryRepo.save(serviceRepo0);
        serviceRepositoryRepo.save(serviceRepo1);
        serviceRepositoryRepo.save(serviceRepo2);

        List<Integer> expectedIds = new ArrayList<Integer>();
        expectedIds.add(serviceRepo0.getId());
        expectedIds.add(serviceRepo1.getId());
        expectedIds.add(serviceRepo2.getId());

        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return serviceRepositoryRepo.getIds().size();
            }
        }.call(expectedIds.size());

        verifyFindById(serviceRepo0);
        verifyFindById(serviceRepo1);
        verifyFindById(serviceRepo2);

        verifyFindByVersionAndPlan(version1, servicePlan0, serviceRepo0);
        verifyFindByVersionAndPlan(version2, servicePlan1, serviceRepo1);

        // test concurrency
        verifyCounter(serviceRepo0, 1);

        ServiceRepository serviceRepo = null;
        while (null == serviceRepo) {
            serviceRepo = serviceRepositoryRepo.findById(serviceRepo0.getId());
        }
        Assert.assertNotNull(serviceRepo);

        boolean thrown = false;
        try {
            serviceRepositoryRepo.save(serviceRepo);
            serviceRepositoryRepo.save(serviceRepo);
            serviceRepositoryRepo.save(serviceRepo);
            serviceRepositoryRepo.save(serviceRepo);

        } catch (DBConcurrentUpdateException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);

        verifyCounter(serviceRepo0, 2);
    }

    @Test
    public void testDelete() throws Exception {
        ServiceRepository serviceRepo0 = createServiceRepo(0,
                                                           verifiedType,
                                                           ServicePlan.STARTER_MEDIA,
                                                           "0.0");
        serviceRepositoryRepo.save(serviceRepo0);
        verifyRepoSize(serviceRepositoryRepo, 1);

        serviceRepositoryRepo.delete(serviceRepo0.getId());
        verifyRepoSize(serviceRepositoryRepo, 0);
    }

    private ServiceRepository createServiceRepo(int id,
                                                ServiceRepository.
                                                    ServiceRepositoryType type,
                                                ServicePlan servicePlan,
                                                String version) {
        return new ServiceRepository(id,
                                     type,
                                     servicePlan,
                                     hostName,
                                     spaceId,
                                     version,
                                     username,
                                     password);
    }

    private void verifyFindById(final ServiceRepository serviceRepo) {
        new DBCaller<ServiceRepository>() {
            protected ServiceRepository doCall() throws Exception {
                return serviceRepositoryRepo.findById(serviceRepo.getId());
            }
        }.call(serviceRepo);
    }

    private void verifyCounter(final ServiceRepository serviceRepo, final int counter) {
        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return serviceRepositoryRepo.findById(serviceRepo.getId()).getCounter();
            }
        }.call(counter);
    }

    private void verifyFindByVersionAndPlan(final String version,
                                            final ServicePlan servicePlan,
                                            final ServiceRepository serviceRepo) {
        new DBCaller<ServiceRepository>() {
            protected ServiceRepository doCall() throws Exception {
                return serviceRepositoryRepo.findByVersionAndPlan(version,
                                                                  servicePlan);
            }
        }.call(serviceRepo);
    }

}