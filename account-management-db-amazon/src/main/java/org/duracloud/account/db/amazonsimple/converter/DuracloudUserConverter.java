/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.util.FormatUtil;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;

/**
 * This class is responsible for converting DuracloudUser objects to/from
 * AmazonSimpleDB attributes.
 *
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserConverter extends BaseDomainConverter implements DomainConverter<DuracloudUser> {

    public DuracloudUserConverter() {
        log = LoggerFactory.getLogger(DuracloudUserConverter.class);
    }

    public static final String USERNAME_ATT = "USERNAME";
    protected static final String PASSWORD_ATT = "PASSWORD";
    protected static final String FIRSTNAME_ATT = "FIRSTNAME";
    protected static final String LASTNAME_ATT = "LASTNAME";
    protected static final String EMAIL_ATT = "EMAIL";

    @Override
    public List<ReplaceableAttribute> toAttributesAndIncrement(DuracloudUser user) {
        List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();

        String counter = FormatUtil.padded(user.getCounter() + 1);

        atts.add(new ReplaceableAttribute(USERNAME_ATT,
                                          user.getUsername(),
                                          true));
        atts.add(new ReplaceableAttribute(PASSWORD_ATT,
                                          user.getPassword(),
                                          true));
        atts.add(new ReplaceableAttribute(FIRSTNAME_ATT,
                                          user.getFirstName(),
                                          true));
        atts.add(new ReplaceableAttribute(LASTNAME_ATT,
                                          user.getLastName(),
                                          true));
        atts.add(new ReplaceableAttribute(EMAIL_ATT, user.getEmail(), true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    @Override
    public DuracloudUser fromAttributes(Collection<Attribute> atts, int id) {
        int counter = -1;
        String username = null;
        String password = null;
        String firstname = null;
        String lastname = null;
        String email = null;

        for (Attribute att : atts) {
            String name = att.getName();
            String value = att.getValue();
            if (COUNTER_ATT.equals(name)) {
                counter = SimpleDBUtils.decodeZeroPaddingInt(value);

            } else if (USERNAME_ATT.equals(name)) {
                username = value;

            } else if (PASSWORD_ATT.equals(name)) {
                password = value;

            } else if (FIRSTNAME_ATT.equals(name)) {
                firstname = value;

            } else if (LASTNAME_ATT.equals(name)) {
                lastname = value;

            } else if (EMAIL_ATT.equals(name)) {
                email = value;

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

        return new DuracloudUser(id,
                                 username,
                                 password,
                                 firstname,
                                 lastname,
                                 email,
                                 counter);
    }
}
