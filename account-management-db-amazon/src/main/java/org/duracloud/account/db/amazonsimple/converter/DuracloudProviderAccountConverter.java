/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.common.domain.ProviderAccount;
import org.duracloud.account.db.util.FormatUtil;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;

/**
 * @author: Bill Branan
 * Date: Feb 1, 2011
 */
public class DuracloudProviderAccountConverter extends BaseDomainConverter
    implements DomainConverter<ProviderAccount> {

    public DuracloudProviderAccountConverter() {
        log = LoggerFactory.getLogger(DuracloudProviderAccountConverter.class);
    }

    protected static final String PROVIDER_TYPE_ATT = "PROVIDER_TYPE";
    protected static final String USERNAME_ATT = "USERNAME";
    protected static final String PASSWORD_ATT = "PASSWORD";

    @Override
    public List<ReplaceableAttribute> toAttributesAndIncrement(
        ProviderAccount account) {
        List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();

        String counter = FormatUtil.padded(account.getCounter() + 1);
        atts.add(new ReplaceableAttribute(
            PROVIDER_TYPE_ATT,
            asString(account.getProviderType()),
            true));
        atts.add(new ReplaceableAttribute(
            USERNAME_ATT,
            account.getUsername(),
            true));
        atts.add(new ReplaceableAttribute(
            PASSWORD_ATT,
            account.getPassword(),
            true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    protected String asString(ProviderAccount.ProviderType providerType) {
        return providerType.name();
    }

    @Override
    public ProviderAccount fromAttributes(Collection<Attribute> atts, int id) {
        int counter = -1;

        ProviderAccount.ProviderType providerType = null;
        String username = null;
        String password = null;

        for (Attribute att : atts) {
            String name = att.getName();
            String value = att.getValue();
            if (COUNTER_ATT.equals(name)) {
                counter = SimpleDBUtils.decodeZeroPaddingInt(value);

            } else if (PROVIDER_TYPE_ATT.equals(name)) {
                providerType =  typeFromString(value);

            } else if (USERNAME_ATT.equals(name)) {
                username = value;

            } else if (PASSWORD_ATT.equals(name)) {
                password = value;

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

        return new ProviderAccount(id,
                               providerType,
                               username,
                               password,
                               counter);
    }

    protected ProviderAccount.ProviderType typeFromString(String strType) {
        return ProviderAccount.ProviderType.valueOf(strType.trim());
    }

}
