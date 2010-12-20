/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.storage.domain.StorageProviderType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.ACCT_NAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.DELIM;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.DEPARTMENT_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.INSTANCE_IDS_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.ORG_NAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.PAYMENT_INFO_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.STORAGE_PROVIDERS_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.SUBDOMAIN_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author: Bill Branan
 * Date: Dec 3, 2010
 */
public class DuraCloudAccountConverterTest extends DomainConverterTest<AccountInfo> {

    private static final int id = 0;

    private static final String subdomain = "subdomain";
    private static final String acctName = "account-name";
    private static final String orgName = "org-name";
    private static final String department = "department";
    private static final int paymentInfoId = 100;
    private static Set<Integer> instanceIds = null;
    private static Set<StorageProviderType> storageProviders = null;
    private static final int counter = 4;

    @BeforeClass
    public static void initialize() throws Exception {
        instanceIds = new HashSet<Integer>();
        instanceIds.add(new Integer(1));
        instanceIds.add(new Integer(2));

        storageProviders = new HashSet<StorageProviderType>();
        storageProviders.add(StorageProviderType.AMAZON_S3);
        storageProviders.add(StorageProviderType.RACKSPACE);
    }

    @Override
    protected DomainConverter<AccountInfo> createConverter() {
        return createAccountConverter();
    }

    private DuracloudAccountConverter createAccountConverter() {
        return new DuracloudAccountConverter();
    }

    @Override
    protected AccountInfo createTestItem() {
        return new AccountInfo(id,
                               subdomain,
                               acctName,
                               orgName,
                               department,
                               paymentInfoId,
                               instanceIds,
                               storageProviders,
                               counter);
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        DuracloudAccountConverter acctCvtr = createAccountConverter();

        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(SUBDOMAIN_ATT, subdomain));
        testAtts.add(new Attribute(ACCT_NAME_ATT, acctName));
        testAtts.add(new Attribute(ORG_NAME_ATT, orgName));
        testAtts.add(new Attribute(DEPARTMENT_ATT, department));
        testAtts.add(new Attribute(PAYMENT_INFO_ID_ATT,
                                   acctCvtr.asString(paymentInfoId)));
        testAtts.add(new Attribute(INSTANCE_IDS_ATT,
                                   acctCvtr.idsAsString(instanceIds)));
        testAtts.add(new Attribute(STORAGE_PROVIDERS_ATT,
                                   acctCvtr.asString(storageProviders)));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(AccountInfo acct) {
        assertNotNull(acct);

        assertNotNull(acct.getCounter());
        assertNotNull(acct.getSubdomain());
        assertNotNull(acct.getAcctName());
        assertNotNull(acct.getOrgName());
        assertNotNull(acct.getDepartment());
        assertNotNull(acct.getPaymentInfoId());
        assertNotNull(acct.getInstanceIds());
        assertNotNull(acct.getStorageProviders());

        assertEquals(counter, acct.getCounter());
        assertEquals(subdomain, acct.getSubdomain());
        assertEquals(acctName, acct.getAcctName());
        assertEquals(orgName, acct.getOrgName());
        assertEquals(department, acct.getDepartment());
        assertEquals(paymentInfoId, acct.getPaymentInfoId());
        assertEquals(instanceIds, acct.getInstanceIds());
        assertEquals(storageProviders, acct.getStorageProviders());            
    }

    @Test
    public void testAsString() {
        DuracloudAccountConverter acctCvtr = createAccountConverter();

        String spString = acctCvtr.asString(storageProviders);
        assertEquals(2, spString.split(DELIM).length);
        assertTrue(spString.contains(StorageProviderType.AMAZON_S3.name()));
        assertTrue(spString.contains(StorageProviderType.RACKSPACE.name()));
    }

    @Test
    public void testFromString() {
        DuracloudAccountConverter acctCvtr = createAccountConverter();

        String spString = StorageProviderType.AMAZON_S3.name() + DELIM +
                          StorageProviderType.RACKSPACE.name();
        Set<StorageProviderType> spSet = acctCvtr.fromString(spString);
        assertEquals(storageProviders, spSet);
    }

}
