/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import junit.framework.Assert;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.computeprovider.domain.ComputeProviderType;

import java.util.ArrayList;
import java.util.List;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudComputeProviderAccountConverter.ELASTIC_IP_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudComputeProviderAccountConverter.KEYPAIR_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudComputeProviderAccountConverter.PASSWORD_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudComputeProviderAccountConverter.PROVIDER_TYPE_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudComputeProviderAccountConverter.SECURITY_GROUP_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudComputeProviderAccountConverter.USERNAME_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author: Bill Branan
 * Date: 3/24/11
 */
public class DuracloudComputeProviderAccountConverterTest extends DomainConverterTest<ComputeProviderAccount> {

    private static final int id = 0;
    private static final ComputeProviderType providerType =
        ComputeProviderType.AMAZON_EC2;
    private static final String username = "username";
    private static final String password = "password";
    private static final String elasticIp = "elasticIp";
    private static final String securityGroup = "securityGroup";
    private static final String keypair = "keypair";
    private static final int counter = 4;

    @Override
    protected DomainConverter<ComputeProviderAccount> createConverter() {
        return new DuracloudComputeProviderAccountConverter();
    }

    @Override
    protected ComputeProviderAccount createTestItem() {
        return new ComputeProviderAccount(id,
                                          providerType,
                                          username,
                                          password,
                                          elasticIp,
                                          securityGroup,
                                          keypair,
                                          counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        DuracloudComputeProviderAccountConverter pvdAcctCvtr =
            new DuracloudComputeProviderAccountConverter();

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(PROVIDER_TYPE_ATT, pvdAcctCvtr.asString(providerType)));
        testAtts.add(new Attribute(USERNAME_ATT, username));
        testAtts.add(new Attribute(PASSWORD_ATT, password));
        testAtts.add(new Attribute(ELASTIC_IP_ATT, elasticIp));
        testAtts.add(new Attribute(SECURITY_GROUP_ATT, securityGroup));
        testAtts.add(new Attribute(KEYPAIR_ATT, keypair));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(ComputeProviderAccount providerAccount) {
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

