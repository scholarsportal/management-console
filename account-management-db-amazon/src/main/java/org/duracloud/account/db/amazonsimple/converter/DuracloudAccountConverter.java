/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.util.FormatUtil;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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

    protected static final String SUBDOMAIN_ATT = "SUBDOMAIN";
    protected static final String ACCT_NAME_ATT = "ACCT_NAME";
    protected static final String ORG_NAME_ATT = "ORG_NAME";
    protected static final String DEPARTMENT_ATT = "DEPARTMENT";
    protected static final String STORAGE_PROVIDERS_ATT = "STORAGE_PROVIDERS";
    protected static final String PAYMENT_INFO_ID_ATT = "PAYMENT_INFO_ID";
    protected static final String INSTANCE_IDS_ATT = "INSTANCE_IDS";

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
        atts.add(new ReplaceableAttribute(STORAGE_PROVIDERS_ATT,
                                          asString(acct.getStorageProviders()),
                                          true));
        atts.add(new ReplaceableAttribute(PAYMENT_INFO_ID_ATT,
                                          asString(acct.getPaymentInfoId()),
                                          true));
        atts.add(new ReplaceableAttribute(INSTANCE_IDS_ATT,
                                          instIdsAsString(acct.getInstanceIds()),
                                          true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    /**
     * This method formats the set of storage providers as follows:
     * storageProvider0,storageProvider1,storageProviderN
     *
     * @param spSet set of storage provider types
     * @return the string value of the list
     */
    protected String asString(Set<StorageProviderType> spSet) {
        StringBuilder builder = new StringBuilder();
        if(null != spSet) {
            for(StorageProviderType sp : spSet) {
                if(builder.length() > 0) {
                    builder.append(DELIM);
                }
                builder.append(sp.name());
            }
        }
        return builder.toString();
    }

    protected String asString(int intVal) {
        return String.valueOf(intVal);
    }

    /**
     * This method formats a set of Integers as follows:
     * int1,int2,intN
     *
     * @param intSet set of integers
     * @return the string value of the set
     */
    protected String instIdsAsString(Set<Integer> intSet) {
        StringBuilder builder = new StringBuilder();
        if(null != intSet) {
            for(Integer in : intSet) {
                if(builder.length() > 0) {
                    builder.append(DELIM);
                }
                builder.append(in.toString());
            }
        }
        return builder.toString();
    }

    @Override
    public AccountInfo fromAttributes(Collection<Attribute> atts, int id) {
        int counter = -1;
        String subdomain = null;
        String acctName = null;
        String orgName = null;
        String department = null;
        int paymentInfoId = -1;
        Set<Integer> instanceIds = null;
        Set<StorageProviderType> storageProviders = null;

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

            } else if (STORAGE_PROVIDERS_ATT.equals(name)) {
                storageProviders = fromString(value);

            } else if (PAYMENT_INFO_ID_ATT.equals(name)) {
                paymentInfoId =
                    idFromString(value, "Payment Info", "Account ID", id);

            } else if (INSTANCE_IDS_ATT.equals(name)) {
                instanceIds = instanceIdsFromString(value, id);

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

        AccountInfo account = new AccountInfo(id,
                                              subdomain,
                                              acctName,
                                              orgName,
                                              department,
                                              paymentInfoId,
                                              instanceIds,
                                              storageProviders,
                                              counter);
        return account;
    }

    protected Set<StorageProviderType> fromString(String value) {
        Set<StorageProviderType> set = new HashSet<StorageProviderType>();
        if(value != null) {
            String[] splitValue = value.split(DELIM);
            for(String spt : splitValue) {
                set.add(StorageProviderType.fromString(spt));
            }
        }
        return set;
    }

    protected Set<Integer> instanceIdsFromString(String value, int acctId) {
        Set<Integer> set = new HashSet<Integer>();
        if(value != null) {
            String[] splitValue = value.split(DELIM);
            for(String instanceId : splitValue) {
                try {
                    set.add(Integer.valueOf(instanceId));                    
                } catch(NumberFormatException e) {
                    log.error("Instance ID value for account " + acctId +
                              " is not a valid integer: " + instanceId);
                }

            }
        }
        return set;
    }

}
