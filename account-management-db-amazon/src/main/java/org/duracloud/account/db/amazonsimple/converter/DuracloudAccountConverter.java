/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.db.util.FormatUtil;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    protected static final String PAYMENT_INFO_ID_ATT = "PAYMENT_INFO_ID";
    protected static final String STATUS_ATT = "STATUS";
    protected static final String TYPE_ATT = "TYPE";
    protected static final String SERVER_DETAILS_ID_ATT = "SERVER_DETAILS_ID";

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
        atts.add(new ReplaceableAttribute(PAYMENT_INFO_ID_ATT,
                                          asString(acct.getPaymentInfoId()),
                                          true));
        atts.add(new ReplaceableAttribute(STATUS_ATT,
                                          acct.getStatus().name(),
                                          true));
        atts.add(new ReplaceableAttribute(TYPE_ATT,
                                          acct.getType().name(),
                                          true));
        atts.add(new ReplaceableAttribute(SERVER_DETAILS_ID_ATT,
                                          asString(acct.getServerDetailsId()),
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
        AccountInfo.AccountStatus status = null;
        int paymentInfoId = -1;
        AccountType type = null;
        int serverDetailsId = -1;

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

            } else if (PAYMENT_INFO_ID_ATT.equals(name)) {
                paymentInfoId =
                    idFromString(value, "Payment Info", "Account", id);

            } else if (STATUS_ATT.equals(name)) {
                status = AccountInfo.AccountStatus.valueOf(value);

            } else if (TYPE_ATT.equals(name)) {
                type = AccountType.valueOf(value);

            } else if (SERVER_DETAILS_ID_ATT.equals(name)) {
                serverDetailsId =
                    idFromString(value, "Server Details", "Account", id);               

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
                               paymentInfoId,
                               serverDetailsId,
                               status,
                               type,
                               counter);
    }

}
