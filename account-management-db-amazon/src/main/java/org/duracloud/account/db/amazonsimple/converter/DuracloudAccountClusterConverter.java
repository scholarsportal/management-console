/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.db.util.FormatUtil;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;

/**
 * @author: Bill Branan
 * Date: 2/16/12
 */
public class DuracloudAccountClusterConverter extends BaseDomainConverter
    implements DomainConverter<AccountCluster> {

    public DuracloudAccountClusterConverter() {
        log = LoggerFactory.getLogger(DuracloudAccountClusterConverter.class);
    }

    protected static final String CLUSTER_NAME_ATT =
        "CLUSTER_NAME";
    protected static final String CLUSTER_ACCOUNT_IDS_ATT =
        "CLUSTER_ACCOUNT_IDS";

    @Override
    public List<ReplaceableAttribute> toAttributesAndIncrement(AccountCluster details) {
        List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();

        String counter = FormatUtil.padded(details.getCounter() + 1);
        atts.add(new ReplaceableAttribute(
            CLUSTER_NAME_ATT,
            details.getClusterName(),
            true));
        atts.add(new ReplaceableAttribute(
            CLUSTER_ACCOUNT_IDS_ATT,
            idsAsString(details.getClusterAccountIds()),
            true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    @Override
    public AccountCluster fromAttributes(Collection<Attribute> atts, int id) {
        int counter = -1;
        String clusterName = null;
        Set<Integer> clusterAccountIds = null;

        for (Attribute att : atts) {
            String name = att.getName();
            String value = att.getValue();
            if (COUNTER_ATT.equals(name)) {
                counter = SimpleDBUtils.decodeZeroPaddingInt(value);

            } else if (CLUSTER_NAME_ATT.equals(name)) {
                clusterName = value;

            } else if (CLUSTER_ACCOUNT_IDS_ATT.equals(name)) {
                clusterAccountIds = idsFromString(value);

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

        return new AccountCluster(id,
                                 clusterName,
                                 clusterAccountIds,
                                 counter);
    }
}
