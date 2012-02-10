/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.common.domain.ServicePlan;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServerDetailsConverter.COMPUTE_PROVIDER_ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServerDetailsConverter.PRIMARY_STORAGE_PROVIDER_ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServerDetailsConverter.SECONDARY_SERVICE_REPOSITORY_IDS_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServerDetailsConverter.SECONDARY_STORAGE_PROVIDER_ACCOUNT_IDS_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServerDetailsConverter.SERVICE_PLAN_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author: Bill Branan
 * Date: 2/8/12
 */
public class DuracloudServerDetailsConverterTest
    extends DomainConverterTest<ServerDetails> {

    private static final int id = 0;

    private static final int computeProviderAccountId = 1;
    private static final int primaryStorageProviderAccountId = 5;
    private static Set<Integer> secondaryStorageProviderAccountIds = null;
    private static Set<Integer> secondaryServiceRepositoryIds = null;
    private static ServicePlan servicePlan = ServicePlan.ALL;
    private static final int counter = 4;

    @BeforeClass
    public static void initialize() throws Exception {
        secondaryStorageProviderAccountIds = new HashSet<Integer>();
        secondaryStorageProviderAccountIds.add(10);
        secondaryStorageProviderAccountIds.add(15);

        secondaryServiceRepositoryIds = new HashSet<Integer>();
        secondaryServiceRepositoryIds.add(1);
        secondaryServiceRepositoryIds.add(2);
    }

    @Override
    protected DomainConverter<ServerDetails> createConverter() {
        return createServerDetailsConverter();
    }

    private DuracloudServerDetailsConverter createServerDetailsConverter() {
        return new DuracloudServerDetailsConverter();
    }

    @Override
    protected ServerDetails createTestItem() {
        return new ServerDetails(id,
                                 computeProviderAccountId,
                                 primaryStorageProviderAccountId,
                                 secondaryStorageProviderAccountIds,
                                 secondaryServiceRepositoryIds,
                                 servicePlan,
                                 counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        DuracloudServerDetailsConverter converter =
            createServerDetailsConverter();

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(COMPUTE_PROVIDER_ACCOUNT_ID_ATT,
                                   converter.asString(computeProviderAccountId)));
        testAtts.add(new Attribute(PRIMARY_STORAGE_PROVIDER_ACCOUNT_ID_ATT,
                                   converter.asString(primaryStorageProviderAccountId)));
        testAtts.add(new Attribute(SECONDARY_STORAGE_PROVIDER_ACCOUNT_IDS_ATT,
                                   converter.idsAsString(
                                       secondaryStorageProviderAccountIds)));
        testAtts.add(new Attribute(SECONDARY_SERVICE_REPOSITORY_IDS_ATT,
                                   converter.idsAsString(secondaryServiceRepositoryIds)));
        testAtts.add(new Attribute(SERVICE_PLAN_ATT, servicePlan.name()));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(ServerDetails acct) {
        assertNotNull(acct);

        assertNotNull(acct.getCounter());
        assertNotNull(acct.getComputeProviderAccountId());
        assertNotNull(acct.getPrimaryStorageProviderAccountId());
        assertNotNull(acct.getSecondaryStorageProviderAccountIds());
        assertNotNull(acct.getSecondaryServiceRepositoryIds());
        assertNotNull(acct.getServicePlan());

        assertEquals(counter, acct.getCounter());
        assertEquals(computeProviderAccountId,
                     acct.getComputeProviderAccountId());
        assertEquals(primaryStorageProviderAccountId,
                     acct.getPrimaryStorageProviderAccountId());
        assertEquals(secondaryStorageProviderAccountIds,
                     acct.getSecondaryStorageProviderAccountIds());
        assertEquals(secondaryServiceRepositoryIds,
                     acct.getSecondaryServiceRepositoryIds());
        assertEquals(servicePlan, acct.getServicePlan());
    }

}
