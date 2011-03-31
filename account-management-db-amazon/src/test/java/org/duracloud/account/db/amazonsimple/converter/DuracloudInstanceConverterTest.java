/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import junit.framework.Assert;
import org.duracloud.account.common.domain.DuracloudInstance;

import java.util.ArrayList;
import java.util.List;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter.ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter.HOST_NAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter.IMAGE_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter.PROVIDER_INSTANCE_ID_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author: Bill Branan
 * Date: Dec 20, 2010
 */
public class DuracloudInstanceConverterTest extends DomainConverterTest<DuracloudInstance> {

    private static final int id = 0;
    private static final int imageId = 10;
    private static final int accountId = 12;
    private static final String hostName = "host";
    private static final String providerInstanceId = "ABCD";
    private static final int counter = 4;

    @Override
    protected DomainConverter<DuracloudInstance> createConverter() {
        return new DuracloudInstanceConverter();
    }

    @Override
    protected DuracloudInstance createTestItem() {
        return new DuracloudInstance(id,
                                     imageId,
                                     accountId,
                                     hostName,
                                     providerInstanceId,
                                     counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        DuracloudInstanceConverter insCvtr = new DuracloudInstanceConverter();

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(IMAGE_ID_ATT, insCvtr.asString(imageId)));
        testAtts.add(new Attribute(ACCOUNT_ID_ATT, insCvtr.asString(accountId)));
        testAtts.add(new Attribute(HOST_NAME_ATT, hostName));
        testAtts.add(new Attribute(PROVIDER_INSTANCE_ID_ATT, providerInstanceId));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(DuracloudInstance instance) {
        Assert.assertNotNull(instance);

        Assert.assertNotNull(instance.getCounter());
        Assert.assertNotNull(instance.getImageId());
        Assert.assertNotNull(instance.getAccountId());
        Assert.assertNotNull(instance.getHostName());
        Assert.assertNotNull(instance.getProviderInstanceId());

        Assert.assertEquals(counter, instance.getCounter());
        Assert.assertEquals(imageId, instance.getImageId());
        Assert.assertEquals(accountId, instance.getAccountId());
        Assert.assertEquals(hostName, instance.getHostName());
        Assert.assertEquals(providerInstanceId,
                            instance.getProviderInstanceId());
    }

}
