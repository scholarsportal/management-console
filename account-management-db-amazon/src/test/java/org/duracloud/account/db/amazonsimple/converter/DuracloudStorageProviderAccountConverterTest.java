/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import junit.framework.Assert;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.storage.domain.StorageProviderType;

import java.util.ArrayList;
import java.util.List;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudStorageProviderAccountConverter.PASSWORD_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudStorageProviderAccountConverter.PROVIDER_TYPE_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudStorageProviderAccountConverter.USERNAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudStorageProviderAccountConverter.RRS_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author: Bill Branan
 * Date: Feb 2, 2011
 */
public class DuracloudStorageProviderAccountConverterTest extends DomainConverterTest<StorageProviderAccount> {

    private static final int id = 0;
    private static final StorageProviderType providerType =
        StorageProviderType.AMAZON_S3;
    private static final String username = "username";
    private static final String password = "password";
    private static final boolean rrs = true;
    private static final int counter = 4;

    @Override
    protected DomainConverter<StorageProviderAccount> createConverter() {
        return new DuracloudStorageProviderAccountConverter();
    }

    @Override
    protected StorageProviderAccount createTestItem() {
        return new StorageProviderAccount(id,
                                          providerType,
                                          username,
                                          password,
                                          rrs,
                                          counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        DuracloudStorageProviderAccountConverter pvdAcctCvtr =
            new DuracloudStorageProviderAccountConverter();

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(PROVIDER_TYPE_ATT, pvdAcctCvtr.asString(providerType)));
        testAtts.add(new Attribute(USERNAME_ATT, username));
        testAtts.add(new Attribute(PASSWORD_ATT, password));
        testAtts.add(new Attribute(RRS_ATT, String.valueOf(rrs)));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(StorageProviderAccount providerAccount) {
        Assert.assertNotNull(providerAccount);

        Assert.assertNotNull(providerAccount.getProviderType());
        Assert.assertNotNull(providerAccount.getUsername());
        Assert.assertNotNull(providerAccount.getPassword());

        Assert.assertEquals(counter, providerAccount.getCounter());
        Assert.assertEquals(providerType, providerAccount.getProviderType());
        Assert.assertEquals(username, providerAccount.getUsername());
        Assert.assertEquals(password, providerAccount.getPassword());
    }

}
