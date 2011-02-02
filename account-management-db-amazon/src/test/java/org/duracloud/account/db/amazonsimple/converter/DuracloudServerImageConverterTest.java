/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import junit.framework.Assert;
import org.duracloud.account.common.domain.ServerImage;

import java.util.ArrayList;
import java.util.List;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServerImageConverter.DESCRIPTION;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServerImageConverter.PROVIDER_ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServerImageConverter.PROVIDER_IMAGE_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudServerImageConverter.VERSION;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author: Bill Branan
 * Date: Feb 2, 2011
 */
public class DuracloudServerImageConverterTest extends DomainConverterTest<ServerImage> {

    private static final int id = 0;
    private static final int providerAccountId = 1;
    private static final String providerImageId = "provider-image-id";
    private static final String version = "version-1";
    private static final String description = "description";
    private static final int counter = 4;

    @Override
    protected DomainConverter<ServerImage> createConverter() {
        return new DuracloudServerImageConverter();
    }

    @Override
    protected ServerImage createTestItem() {
        return new ServerImage(id,
                               providerAccountId,
                               providerImageId,
                               version,
                               description,
                               counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        DuracloudServerImageConverter imgCvtr =
            new DuracloudServerImageConverter();

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(PROVIDER_ACCOUNT_ID_ATT, imgCvtr.asString(providerAccountId)));
        testAtts.add(new Attribute(PROVIDER_IMAGE_ID_ATT, providerImageId));
        testAtts.add(new Attribute(VERSION, version));
        testAtts.add(new Attribute(DESCRIPTION, description));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(ServerImage serverImage) {
        Assert.assertNotNull(serverImage);

        Assert.assertNotNull(serverImage.getProviderAccountId());
        Assert.assertNotNull(serverImage.getProviderImageId());
        Assert.assertNotNull(serverImage.getVersion());
        Assert.assertNotNull(serverImage.getDescription());

        Assert.assertEquals(counter, serverImage.getCounter());
        Assert.assertEquals(providerAccountId, serverImage.getProviderAccountId());
        Assert.assertEquals(providerImageId, serverImage.getProviderImageId());
        Assert.assertEquals(version, serverImage.getVersion());
        Assert.assertEquals(description, serverImage.getDescription());

    }

}