/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.util.FormatUtil;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;

/**
 * This class is responsible for converting UserInvitation objects to/from
 * AmazonSimpleDB attributes.
 * 
 * @author Daniel Bernstein
 *  Date: Dec 15, 2010
 */
public class DuracloudUserInvitationConverter extends BaseDomainConverter
    implements DomainConverter<UserInvitation> {

    public DuracloudUserInvitationConverter() {
        log = LoggerFactory.getLogger(DuracloudUserInvitationConverter.class);
    }

    protected static final String ACCOUNT_ID_ATT = "ACCOUNT_ID";
    protected static final String USER_EMAIL_ATT = "USER_EMAIL";
    protected static final String CREATIONDATE_ATT = "CREATION_DATE";
    protected static final String EXPIRATION_DATE_ATT = "EXPIRATION_DATE";
    public static final String REDEMPTION_CODE_ATT = "REDEMPTION_CODE";

    @Override
    public List<ReplaceableAttribute> toAttributesAndIncrement(
        UserInvitation userInvitation) {
        List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();
        String counter = FormatUtil.padded(userInvitation.getCounter() + 1);
        
        atts.add(new ReplaceableAttribute(
            ACCOUNT_ID_ATT, asString(userInvitation.getAccountId()), true));
        atts.add(new ReplaceableAttribute(
            USER_EMAIL_ATT, userInvitation.getUserEmail(), true));
        atts
            .add(new ReplaceableAttribute(
                CREATIONDATE_ATT, asString(userInvitation.getCreationDate()),
                true));
        atts.add(new ReplaceableAttribute(
            EXPIRATION_DATE_ATT, asString(userInvitation.getExpirationDate()),
            true));
        atts.add(new ReplaceableAttribute(REDEMPTION_CODE_ATT, userInvitation
            .getRedemptionCode(), true));
        atts.add(new ReplaceableAttribute(COUNTER_ATT, counter, true));

        return atts;
    }

    @Override
    public UserInvitation fromAttributes(Collection<Attribute> atts, int id) {
        int counter = -1;
        int accountId = -1;
        String userEmail = null;
        Date creationDate = null;
        Date expirationDate = null;
        String redemptionCode = null;

        for (Attribute att : atts) {
            String name = att.getName();
            String value = att.getValue();
            if (COUNTER_ATT.equals(name)) {
                counter = SimpleDBUtils.decodeZeroPaddingInt(value);

            } else if (ACCOUNT_ID_ATT.equals(name)) {
                accountId =
                    idFromString(value, "Account", "User Invitation ID", id);

            } else if (USER_EMAIL_ATT.equals(name)) {
                userEmail = value;

            } else if (CREATIONDATE_ATT.equals(name)) {
                creationDate = dateFromString(value);

            } else if (EXPIRATION_DATE_ATT.equals(name)) {
                expirationDate = dateFromString(value);

            } else if (REDEMPTION_CODE_ATT.equals(name)) {
                redemptionCode = value;

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

        return new UserInvitation(
            id, counter, accountId, userEmail, creationDate, expirationDate,
            redemptionCode);

    }
}
