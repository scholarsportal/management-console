/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.db.util.FormatUtil;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;

/**
 * This class is responsible for converting DuracloudGroup objects to/from
 * AmazonSimpleDB attributes.
 *
 * @author Andrew Woods
 *         Date: Nov 12, 2011
 */
public class DuracloudGroupConverter extends BaseDomainConverter implements DomainConverter<DuracloudGroup> {

    public DuracloudGroupConverter() {
        log = LoggerFactory.getLogger(DuracloudGroupConverter.class);
    }

    public static final String GROUPNAME_ATT = "GROUPNAME";
    public static final String USERS_ATT = "USERS";
    public static final String ACCOUNT_ID_ATT = "ACCOUNT_ID";

    @Override
    public List<ReplaceableAttribute> toAttributesAndIncrement(DuracloudGroup group) {
        List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();

        String counter = FormatUtil.padded(group.getCounter() + 1);

        atts.add(new ReplaceableAttribute(GROUPNAME_ATT,
                                          group.getName(),
                                          true));
        atts.add(new ReplaceableAttribute(ACCOUNT_ID_ATT,
                                          asString(group.getAccountId()),
                                          true));
        atts.add(new ReplaceableAttribute(USERS_ATT,
                                          idsAsString(group.getUserIds()),
                                          true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    @Override
    public DuracloudGroup fromAttributes(Collection<Attribute> atts, int id) {
        int counter = -1;
        String groupname = null;
        Set<Integer> userIds = null;
        int accountId = -1;

        for (Attribute att : atts) {
            String name = att.getName();
            String value = att.getValue();
            if (COUNTER_ATT.equals(name)) {
                counter = SimpleDBUtils.decodeZeroPaddingInt(value);

            } else if (GROUPNAME_ATT.equals(name)) {
                groupname = value;

            } else if (USERS_ATT.equals(name)) {
                userIds = idsFromString(value);

            } else if (ACCOUNT_ID_ATT.equals(name)) {
                accountId = idFromString(value, "Account", "Group", id);

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

        return new DuracloudGroup(id, groupname, accountId, userIds, counter);
    }
}
