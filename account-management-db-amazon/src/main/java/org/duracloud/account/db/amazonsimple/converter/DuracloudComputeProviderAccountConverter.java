/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.util.FormatUtil;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;

/**
 * @author: Bill Branan
 * Date: 3/24/11
 */
public class DuracloudComputeProviderAccountConverter extends BaseDomainConverter
    implements DomainConverter<ComputeProviderAccount> {

    public DuracloudComputeProviderAccountConverter() {
        log = LoggerFactory
            .getLogger(DuracloudComputeProviderAccountConverter.class);
    }

    protected static final String PROVIDER_TYPE_ATT = "PROVIDER_TYPE";
    protected static final String USERNAME_ATT = "USERNAME";
    protected static final String PASSWORD_ATT = "PASSWORD";
    protected static final String ELASTIC_IP_ATT = "ELASTIC_IP";
    protected static final String SECURITY_GROUP_ATT = "SECURITY_GROUP";
    protected static final String KEYPAIR_ATT = "KEYPAIR";

    @Override
    public List<ReplaceableAttribute> toAttributesAndIncrement(
        ComputeProviderAccount account) {
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
        atts.add(new ReplaceableAttribute(
            ELASTIC_IP_ATT,
            account.getElasticIp(),
            true));
        atts.add(new ReplaceableAttribute(
            SECURITY_GROUP_ATT,
            account.getSecurityGroup(),
            true));
        atts.add(new ReplaceableAttribute(
            KEYPAIR_ATT,
            account.getKeypair(),
            true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    protected String asString(ComputeProviderType providerType) {
        return providerType.name();
    }

    @Override
    public ComputeProviderAccount fromAttributes(Collection<Attribute> atts, int id) {
        int counter = -1;

        ComputeProviderType providerType = null;
        String username = null;
        String password = null;
        String elasticIp = null;
        String securityGroup = null;
        String keypair = null;

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

            } else if (ELASTIC_IP_ATT.equals(name)) {
                elasticIp = value;

            } else if (SECURITY_GROUP_ATT.equals(name)) {
                securityGroup = value;

            } else if (KEYPAIR_ATT.equals(name)) {
                keypair = value;                

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

        return new ComputeProviderAccount(id,
                               providerType,
                               username,
                               password,
                               elasticIp,
                               securityGroup,
                               keypair,
                               counter);
    }

    protected ComputeProviderType typeFromString(String strType) {
        return ComputeProviderType.valueOf(strType.trim());
    }

}
