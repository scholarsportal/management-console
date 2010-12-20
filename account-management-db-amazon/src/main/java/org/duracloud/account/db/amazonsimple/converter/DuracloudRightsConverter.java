/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.util.FormatUtil;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;

/**
 * @author: Bill Branan
 * Date: Dec 2, 2010
 */
public class DuracloudRightsConverter extends BaseDomainConverter implements DomainConverter<AccountRights> {

    public DuracloudRightsConverter() {
        log = LoggerFactory.getLogger(DuracloudRightsConverter.class);
    }

    public static final String ACCOUNT_ID_ATT = "ACCOUNT_ID";
    public static final String USER_ID_ATT = "USER_ID";
    protected static final String ROLES_ATT = "ROLES";

    @Override
    public List<ReplaceableAttribute> toAttributesAndIncrement(AccountRights rights) {
        List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();

        String counter = FormatUtil.padded(rights.getCounter() + 1);
        atts.add(new ReplaceableAttribute(ACCOUNT_ID_ATT,
                                          asString(rights.getAccountId()),
                                          true));
        atts.add(new ReplaceableAttribute(USER_ID_ATT,
                                          asString(rights.getUserId()),
                                          true));
        atts.add(new ReplaceableAttribute(ROLES_ATT,
                                          asString(rights.getRoles()),
                                          true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    protected String asString(int intVal) {
        return String.valueOf(intVal);
    }

    /**
     * This method formats a set of Roles as follows:
     * role1,role2,roleN
     *
     * @param roleSet set of roles
     * @return the string value of the set
     */
    protected String asString(Set<Role> roleSet) {
        StringBuilder builder = new StringBuilder();
        for(Role role : roleSet) {
            if(builder.length() > 0) {
                builder.append(DELIM);
            }
            builder.append(role.name());
        }
        return builder.toString();
    }

    @Override
    public AccountRights fromAttributes(Collection<Attribute> atts, int id) {
        int counter = -1;
        int accountId = -1;
        int userId = -1;
        Set<Role> roles = null;

        for (Attribute att : atts) {
            String name = att.getName();
            String value = att.getValue();
            if (COUNTER_ATT.equals(name)) {
                counter = SimpleDBUtils.decodeZeroPaddingInt(value);

            } else if (ACCOUNT_ID_ATT.equals(name)) {
                accountId = idFromString(value, "Account", "Rights", id);

            } else if (USER_ID_ATT.equals(name)) {
                userId = idFromString(value, "User", "Rights", id);

            } else if (ROLES_ATT.equals(name)) {
                roles = fromString(value);

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

        AccountRights rights = new AccountRights(id,
                                                 accountId,
                                                 userId,
                                                 roles,
                                                 counter);
        return rights;
    }

    protected Set<Role> fromString(String value) {
        Set<Role> set = new HashSet<Role>();
        if(value != null) {
            String[] splitValue = value.split(DELIM);
            for(String role : splitValue) {
                set.add(Role.valueOf(role));
            }
        }
        return set;
    }

}
