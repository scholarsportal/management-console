/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountType;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.ACCT_NAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.DEPARTMENT_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.ORG_NAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.PAYMENT_INFO_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.SERVER_DETAILS_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.STATUS_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.SUBDOMAIN_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.TYPE_ATT;
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
    private static final int serverDetailsId = 200;
    private static final AccountInfo.AccountStatus status =
        AccountInfo.AccountStatus.PENDING;
    private static final AccountType type = AccountType.FULL;
    private static final int counter = 4;

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
                               serverDetailsId,
                               status,
                               type,
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
        testAtts.add(new Attribute(SERVER_DETAILS_ID_ATT,
                                   acctCvtr.asString(serverDetailsId)));
        testAtts.add(new Attribute(STATUS_ATT, status.name()));
        testAtts.add(new Attribute(TYPE_ATT, type.name()));
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
        assertNotNull(acct.getServerDetailsId());
        assertNotNull(acct.getStatus());
        assertNotNull(acct.getType());

        assertEquals(counter, acct.getCounter());
        assertEquals(subdomain, acct.getSubdomain());
        assertEquals(acctName, acct.getAcctName());
        assertEquals(orgName, acct.getOrgName());
        assertEquals(department, acct.getDepartment());
        assertEquals(paymentInfoId, acct.getPaymentInfoId());
        assertEquals(serverDetailsId, acct.getServerDetailsId());
        assertEquals(status, acct.getStatus());
        assertEquals(type, acct.getType());
    }

}
