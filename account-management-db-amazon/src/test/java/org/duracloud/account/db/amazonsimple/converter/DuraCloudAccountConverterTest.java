/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.ServicePlan;
import org.junit.BeforeClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.*;
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
    private static final int computeProviderAccountId = 1;
    private static final int primaryStorageProviderAccountId = 5;
    private static Set<Integer> secondaryStorageProviderAccountIds = null;
    private static Set<Integer> secondaryServiceRepositoryIds = null;
    private static final int paymentInfoId = 100;
    private static ServicePlan servicePlan = ServicePlan.PROFESSIONAL;
    private static AccountInfo.AccountStatus status =
        AccountInfo.AccountStatus.PENDING;
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
                               computeProviderAccountId,
                               primaryStorageProviderAccountId,
                               secondaryStorageProviderAccountIds,
                               secondaryServiceRepositoryIds,
                               paymentInfoId,
                               servicePlan,
                               status,
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
        testAtts.add(new Attribute(COMPUTE_PROVIDER_ACCOUNT_ID_ATT,
                                   acctCvtr.asString(computeProviderAccountId)));
        testAtts.add(new Attribute(PRIMARY_STORAGE_PROVIDER_ACCOUNT_ID_ATT,
                                   acctCvtr.asString(primaryStorageProviderAccountId)));
        testAtts.add(new Attribute(SECONDARY_STORAGE_PROVIDER_ACCOUNT_IDS_ATT,
                                   acctCvtr.idsAsString(
                                       secondaryStorageProviderAccountIds)));
        testAtts.add(new Attribute(SECONDARY_SERVICE_REPOSITORY_IDS_ATT,
                                   acctCvtr.idsAsString(secondaryServiceRepositoryIds)));
        testAtts.add(new Attribute(PAYMENT_INFO_ID_ATT,
                                   acctCvtr.asString(paymentInfoId)));
        testAtts.add(new Attribute(PACKAGE_TYPE_ATT, servicePlan.name()));
        testAtts.add(new Attribute(STATUS_ATT, status.name()));
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
        assertNotNull(acct.getComputeProviderAccountId());
        assertNotNull(acct.getPrimaryStorageProviderAccountId());
        assertNotNull(acct.getSecondaryStorageProviderAccountIds());
        assertNotNull(acct.getSecondaryServiceRepositoryIds());
        assertNotNull(acct.getPaymentInfoId());
        assertNotNull(acct.getServicePlan());
        assertNotNull(acct.getStatus());

        assertEquals(counter, acct.getCounter());
        assertEquals(subdomain, acct.getSubdomain());
        assertEquals(acctName, acct.getAcctName());
        assertEquals(orgName, acct.getOrgName());
        assertEquals(department, acct.getDepartment());
        assertEquals(computeProviderAccountId,
                     acct.getComputeProviderAccountId());
        assertEquals(primaryStorageProviderAccountId,
                     acct.getPrimaryStorageProviderAccountId());
        assertEquals(secondaryStorageProviderAccountIds,
                     acct.getSecondaryStorageProviderAccountIds());
        assertEquals(secondaryServiceRepositoryIds,
                     acct.getSecondaryServiceRepositoryIds());
        assertEquals(paymentInfoId, acct.getPaymentInfoId());
        assertEquals(servicePlan, acct.getServicePlan());
        assertEquals(status, acct.getStatus());
    }

}
