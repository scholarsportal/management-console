/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.db.util.FormatUtil;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;

/**
 * @author: Bill Branan
 * Date: 2/8/12
 */
public class DuracloudServerDetailsConverter extends BaseDomainConverter
    implements DomainConverter<ServerDetails> {

    public DuracloudServerDetailsConverter() {
        log = LoggerFactory.getLogger(DuracloudServerDetailsConverter.class);
    }

    protected static final String COMPUTE_PROVIDER_ACCOUNT_ID_ATT =
        "COMPUTE_PROVIDER_ACCOUNT_ID";
    protected static final String PRIMARY_STORAGE_PROVIDER_ACCOUNT_ID_ATT =
        "PRIMARY_STORAGE_PROVIDER_ACCOUNT_ID";
    protected static final String SECONDARY_STORAGE_PROVIDER_ACCOUNT_IDS_ATT =
        "SECONDARY_STORAGE_PROVIDER_ACCOUNT_IDS";
    protected static final String SECONDARY_SERVICE_REPOSITORY_IDS_ATT =
        "SECONDARY_SERVICE_REPOSITORY_IDS";
    protected static final String SERVICE_PLAN_ATT = "SERVICE_PLAN";

    @Override
    public List<ReplaceableAttribute> toAttributesAndIncrement(ServerDetails details) {
        List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();

        String counter = FormatUtil.padded(details.getCounter() + 1);
        atts.add(new ReplaceableAttribute(
            COMPUTE_PROVIDER_ACCOUNT_ID_ATT,
            asString(details.getComputeProviderAccountId()),
            true));
        atts.add(new ReplaceableAttribute(
            PRIMARY_STORAGE_PROVIDER_ACCOUNT_ID_ATT,
            asString(details.getPrimaryStorageProviderAccountId()),
            true));
        atts.add(new ReplaceableAttribute(
            SECONDARY_STORAGE_PROVIDER_ACCOUNT_IDS_ATT,
            idsAsString(details.getSecondaryStorageProviderAccountIds()),
            true));
        atts.add(new ReplaceableAttribute(
            SECONDARY_SERVICE_REPOSITORY_IDS_ATT,
            idsAsString(details.getSecondaryServiceRepositoryIds()),
            true));
        atts.add(new ReplaceableAttribute(SERVICE_PLAN_ATT,
                                          details.getServicePlan().name(),
                                          true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    @Override
    public ServerDetails fromAttributes(Collection<Attribute> atts, int id) {
        int counter = -1;
        int computeProviderAccountId = -1;
        int primaryStorageProviderAccountId = -1;
        Set<Integer> secondaryStorageProviderAccountIds = null;
        Set<Integer> secondaryServiceRepositoryIds = null;
        ServicePlan servicePlan = null;

        for (Attribute att : atts) {
            String name = att.getName();
            String value = att.getValue();
            if (COUNTER_ATT.equals(name)) {
                counter = SimpleDBUtils.decodeZeroPaddingInt(value);

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

            } else if (SERVICE_PLAN_ATT.equals(name)) {
                servicePlan = ServicePlan.fromString(value);

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

        return new ServerDetails(id,
                                 computeProviderAccountId,
                                 primaryStorageProviderAccountId,
                                 secondaryStorageProviderAccountIds,
                                 secondaryServiceRepositoryIds,
                                 servicePlan,
                                 counter);
    }

}
