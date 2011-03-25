/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import junit.framework.Assert;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter.COMPUTE_PROVIDER_ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter.HOST_NAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter.IMAGE_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter.PRIMARY_STORAGE_PROVIDER_ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter.PROVIDER_INSTANCE_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter.SECONDARY_STORAGE_PROVIDER_ACCOUNT_IDS_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter.SERVICE_REPOSITORY_IDS_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author: Bill Branan
 * Date: Dec 20, 2010
 */
public class DuracloudInstanceConverterTest extends DomainConverterTest<DuracloudInstance> {

    private static final int id = 0;
    private static final int imageId = 10;
    private static final String hostName = "host";
    private static final String providerInstanceId = "ABCD";
    private static final int computeProviderAccountId = 1;
    private static final int primaryStorageProviderAccountId = 5;
    private static Set<Integer> secondaryStorageProviderAccountIds = null;
    private static Set<Integer> serviceRepositoryIds = null;
    private static final int counter = 4;

    @BeforeClass
    public static void init() {
        secondaryStorageProviderAccountIds = new HashSet<Integer>();
        secondaryStorageProviderAccountIds.add(10);
        secondaryStorageProviderAccountIds.add(15);

        serviceRepositoryIds = new HashSet<Integer>();
        serviceRepositoryIds.add(1);
        serviceRepositoryIds.add(2);
    }

    @Override
    protected DomainConverter<DuracloudInstance> createConverter() {
        return new DuracloudInstanceConverter();
    }

    @Override
    protected DuracloudInstance createTestItem() {
        return new DuracloudInstance(id,
                                     imageId,
                                     hostName,
                                     providerInstanceId,
                                     computeProviderAccountId,
                                     primaryStorageProviderAccountId,
                                     secondaryStorageProviderAccountIds,
                                     serviceRepositoryIds,
                                     counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        DuracloudInstanceConverter insCvtr = new DuracloudInstanceConverter();

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(IMAGE_ID_ATT, insCvtr.asString(imageId)));
        testAtts.add(new Attribute(HOST_NAME_ATT, hostName));
        testAtts.add(new Attribute(PROVIDER_INSTANCE_ID_ATT, providerInstanceId));
        testAtts.add(new Attribute(COMPUTE_PROVIDER_ACCOUNT_ID_ATT,
                                   insCvtr.asString(computeProviderAccountId)));
        testAtts.add(new Attribute(PRIMARY_STORAGE_PROVIDER_ACCOUNT_ID_ATT,
                                   insCvtr.asString(primaryStorageProviderAccountId)));
        testAtts.add(new Attribute(SECONDARY_STORAGE_PROVIDER_ACCOUNT_IDS_ATT,
                                   insCvtr.idsAsString(secondaryStorageProviderAccountIds)));
        testAtts.add(new Attribute(SERVICE_REPOSITORY_IDS_ATT,
                                   insCvtr.idsAsString(serviceRepositoryIds)));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(DuracloudInstance instance) {
        Assert.assertNotNull(instance);

        Assert.assertNotNull(instance.getCounter());
        Assert.assertNotNull(instance.getImageId());
        Assert.assertNotNull(instance.getHostName());
        Assert.assertNotNull(instance.getProviderInstanceId());
        Assert.assertNotNull(instance.getComputeProviderAccountId());
        Assert.assertNotNull(instance.getPrimaryStorageProviderAccountId());
        Assert.assertNotNull(instance.getSecondaryStorageProviderAccountIds());
        Assert.assertNotNull(instance.getServiceRepositoryIds());

        Assert.assertEquals(counter, instance.getCounter());
        Assert.assertEquals(imageId, instance.getImageId());
        Assert.assertEquals(hostName, instance.getHostName());
        Assert.assertEquals(providerInstanceId,
                            instance.getProviderInstanceId());
        Assert.assertEquals(computeProviderAccountId,
                            instance.getComputeProviderAccountId());        
        Assert.assertEquals(primaryStorageProviderAccountId,
                            instance.getPrimaryStorageProviderAccountId());
        Assert.assertEquals(secondaryStorageProviderAccountIds,
                            instance.getSecondaryStorageProviderAccountIds());
        Assert.assertEquals(serviceRepositoryIds,
                            instance.getServiceRepositoryIds());
    }

}
