/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.util.FormatUtil;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;

/**
 * @author: Bill Branan
 * Date: Dec 1, 2010
 */
public class DuracloudAccountConverter extends BaseDomainConverter implements DomainConverter<AccountInfo> {

    public DuracloudAccountConverter() {
        log = LoggerFactory.getLogger(DuracloudAccountConverter.class);
    }

    public static final String SUBDOMAIN_ATT = "SUBDOMAIN";
    protected static final String ACCT_NAME_ATT = "ACCT_NAME";
    protected static final String ORG_NAME_ATT = "ORG_NAME";
    protected static final String DEPARTMENT_ATT = "DEPARTMENT";
    protected static final String COMPUTE_PROVIDER_ACCOUNT_ID_ATT =
        "COMPUTE_PROVIDER_ACCOUNT_ID";
    protected static final String PRIMARY_STORAGE_PROVIDER_ACCOUNT_ID_ATT =
        "PRIMARY_STORAGE_PROVIDER_ACCOUNT_ID";
    protected static final String SECONDARY_STORAGE_PROVIDER_ACCOUNT_IDS_ATT =
        "SECONDARY_STORAGE_PROVIDER_ACCOUNT_IDS";
    protected static final String SECONDARY_SERVICE_REPOSITORY_IDS_ATT =
        "SECONDARY_SERVICE_REPOSITORY_IDS";
    protected static final String PAYMENT_INFO_ID_ATT = "PAYMENT_INFO_ID";
    protected static final String PACKAGE_TYPE_ATT = "PACKAGE_TYPE";
    protected static final String STATUS_ATT = "STATUS";

    @Override
    public List<ReplaceableAttribute> toAttributesAndIncrement(AccountInfo acct) {
        List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();

        String counter = FormatUtil.padded(acct.getCounter() + 1);
        atts.add(new ReplaceableAttribute(SUBDOMAIN_ATT,
                                          acct.getSubdomain(),
                                          true));
        atts.add(new ReplaceableAttribute(ACCT_NAME_ATT,
                                          acct.getAcctName(),
                                          true));
        atts.add(new ReplaceableAttribute(ORG_NAME_ATT,
                                          acct.getOrgName(),
                                          true));
        atts.add(new ReplaceableAttribute(DEPARTMENT_ATT,
                                          acct.getDepartment(),
                                          true));
        atts.add(new ReplaceableAttribute(
            COMPUTE_PROVIDER_ACCOUNT_ID_ATT,
            asString(acct.getComputeProviderAccountId()),
            true));
        atts.add(new ReplaceableAttribute(
            PRIMARY_STORAGE_PROVIDER_ACCOUNT_ID_ATT,
            asString(acct.getPrimaryStorageProviderAccountId()),
            true));
        atts.add(new ReplaceableAttribute(
            SECONDARY_STORAGE_PROVIDER_ACCOUNT_IDS_ATT,
            idsAsString(acct.getSecondaryStorageProviderAccountIds()),
            true));
        atts.add(new ReplaceableAttribute(
            SECONDARY_SERVICE_REPOSITORY_IDS_ATT,
            idsAsString(acct.getSecondaryServiceRepositoryIds()),
            true));
        atts.add(new ReplaceableAttribute(PAYMENT_INFO_ID_ATT,
                                          asString(acct.getPaymentInfoId()),
                                          true));
        atts.add(new ReplaceableAttribute(PACKAGE_TYPE_ATT,
                                          acct.getPackageType().name(),
                                          true));
        atts.add(new ReplaceableAttribute(STATUS_ATT,
                                          acct.getStatus().name(),
                                          true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    @Override
    public AccountInfo fromAttributes(Collection<Attribute> atts, int id) {
        int counter = -1;
        String subdomain = null;
        String acctName = null;
        String orgName = null;
        String department = null;
        int computeProviderAccountId = -1;
        int primaryStorageProviderAccountId = -1;
        Set<Integer> secondaryStorageProviderAccountIds = null;
        Set<Integer> secondaryServiceRepositoryIds = null;
        AccountInfo.AccountStatus status = null;
        int paymentInfoId = -1;
        AccountInfo.PackageType packageType = null;

        for (Attribute att : atts) {
            String name = att.getName();
            String value = att.getValue();
            if (COUNTER_ATT.equals(name)) {
                counter = SimpleDBUtils.decodeZeroPaddingInt(value);

            } else if (SUBDOMAIN_ATT.equals(name)) {
                subdomain = value;

            } else if (ACCT_NAME_ATT.equals(name)) {
                acctName = value;

            } else if (ORG_NAME_ATT.equals(name)) {
                orgName = value;

            } else if (DEPARTMENT_ATT.equals(name)) {
                department = value;

            } else if (COMPUTE_PROVIDER_ACCOUNT_ID_ATT.equals(name)) {
                computeProviderAccountId =
                    idFromString(value,
                                 "Compute Provider Account",
                                 "Instance",
                                 id);

            } else if (PRIMARY_STORAGE_PROVIDER_ACCOUNT_ID_ATT.equals(name)) {
                primaryStorageProviderAccountId =
                    idFromString(value,
                                 "Primary Storage Provider Account",
                                 "Instance",
                                 id);

            } else if (SECONDARY_STORAGE_PROVIDER_ACCOUNT_IDS_ATT.equals(name)) {
                secondaryStorageProviderAccountIds = idsFromString(value);

            } else if (SECONDARY_SERVICE_REPOSITORY_IDS_ATT.equals(name)) {
                secondaryServiceRepositoryIds = idsFromString(value);

            } else if (PAYMENT_INFO_ID_ATT.equals(name)) {
                paymentInfoId =
                    idFromString(value, "Payment Info", "Account", id);

            } else if (PACKAGE_TYPE_ATT.equals(name)) {
                packageType = AccountInfo.PackageType.valueOf(value);

            } else if (STATUS_ATT.equals(name)) {
                status = AccountInfo.AccountStatus.valueOf(value);

            } else {
                StringBuilder msg = new StringBuilder("Unexpected name: ");
                msg.append(name);
                msg.append(" in domain: ");
                msg.append(getDomain());
                msg.append(" [with id]: ");
                msg.append(id);
                log.info(msg.toString());
            }
        }

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
                               packageType,
                               status,
                               counter);
    }

}
