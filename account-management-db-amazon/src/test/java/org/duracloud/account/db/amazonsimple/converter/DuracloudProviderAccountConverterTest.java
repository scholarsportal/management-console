/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import junit.framework.Assert;
import org.duracloud.account.common.domain.ProviderAccount;

import java.util.ArrayList;
import java.util.List;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudProviderAccountConverter.PROVIDER_TYPE_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudProviderAccountConverter.USERNAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudProviderAccountConverter.PASSWORD_ATT;

import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author: Bill Branan
 * Date: Feb 2, 2011
 */
public class DuracloudProviderAccountConverterTest  extends DomainConverterTest<ProviderAccount> {

    private static final int id = 0;
    private static final ProviderAccount.ProviderType providerType =
        ProviderAccount.ProviderType.AMAZON;
    private static final String username = "username";
    private static final String password = "password";
    private static final int counter = 4;

    @Override
    protected DomainConverter<ProviderAccount> createConverter() {
        return new DuracloudProviderAccountConverter();
    }

    @Override
    protected ProviderAccount createTestItem() {
        return new ProviderAccount(id,
                                   providerType,
                                   username,
                                   password,
                                   counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        DuracloudProviderAccountConverter pvdAcctCvtr =
            new DuracloudProviderAccountConverter();

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(PROVIDER_TYPE_ATT, pvdAcctCvtr.asString(providerType)));
        testAtts.add(new Attribute(USERNAME_ATT, username));
        testAtts.add(new Attribute(PASSWORD_ATT, password));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(ProviderAccount providerAccount) {
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
