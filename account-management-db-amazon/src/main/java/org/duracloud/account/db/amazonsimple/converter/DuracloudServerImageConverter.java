/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.common.domain.ServerImage;
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
public class DuracloudServerImageConverter extends BaseDomainConverter
    implements DomainConverter<ServerImage> {

    public DuracloudServerImageConverter() {
        log = LoggerFactory.getLogger(DuracloudServerImageConverter.class);
    }

    protected static final String PROVIDER_ACCOUNT_ID_ATT =
        "PROVIDER_ACCOUNT_ID";
    protected static final String PROVIDER_IMAGE_ID_ATT = "PROVIDER_IMAGE_ID";
    protected static final String VERSION_ATT = "VERSION";
    protected static final String DESCRIPTION_ATT = "DESCRIPTION";
    protected static final String DC_ROOT_PASSWORD_ATT = "DC_ROOT_PASSWORD";

    @Override
    public List<ReplaceableAttribute> toAttributesAndIncrement(
        ServerImage image) {
        List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();

        String counter = FormatUtil.padded(image.getCounter() + 1);
        atts.add(new ReplaceableAttribute(
            PROVIDER_ACCOUNT_ID_ATT,
            asString(image.getProviderAccountId()),
            true));
        atts.add(new ReplaceableAttribute(
            PROVIDER_IMAGE_ID_ATT,
            image.getProviderImageId(),
            true));
        atts.add(new ReplaceableAttribute(VERSION_ATT,
            image.getVersion(),
            true));
        atts.add(new ReplaceableAttribute(DESCRIPTION_ATT,
            image.getDescription(),
            true));
        atts.add(new ReplaceableAttribute(
            DC_ROOT_PASSWORD_ATT,
            image.getDcRootPassword(),
            true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    @Override
    public ServerImage fromAttributes(Collection<Attribute> atts, int id) {
        int counter = -1;

        int providerAccountId = -1;
        String providerImageId = null;
        String version = null;
        String description = null;
        String dcRootPassword = null;

        for (Attribute att : atts) {
            String name = att.getName();
            String value = att.getValue();
            if (COUNTER_ATT.equals(name)) {
                counter = SimpleDBUtils.decodeZeroPaddingInt(value);

            } else if (PROVIDER_ACCOUNT_ID_ATT.equals(name)) {
                providerAccountId =
                    idFromString(value, "Provider Account", "Server Image", id);

            } else if (PROVIDER_IMAGE_ID_ATT.equals(name)) {
                providerImageId = value;

            } else if (VERSION_ATT.equals(name)) {
                version = value;

            } else if (DESCRIPTION_ATT.equals(name)) {
                description = value;

            } else if (DC_ROOT_PASSWORD_ATT.equals(name)) {
                dcRootPassword = value;

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

        return new ServerImage(id,
                               providerAccountId,
                               providerImageId,
                               version,
                               description,
                               dcRootPassword,
                               counter);
    }

}
