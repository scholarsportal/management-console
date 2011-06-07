/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple.converter;

import com.amazonaws.services.simpledb.model.Attribute;
import org.duracloud.account.common.domain.UserInvitation;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserInvitationConverter.ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserInvitationConverter.ADMIN_USERNAME_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserInvitationConverter.CREATIONDATE_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserInvitationConverter.EXPIRATION_DATE_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserInvitationConverter.REDEMPTION_CODE_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserInvitationConverter.USER_EMAIL_ATT;
import static org.duracloud.account.db.util.FormatUtil.padded;

/**
 * @author Daniel Bernstein Date: Dec 15, 2010
 */
public class DuracloudUserInvitationConverterTest
    extends DomainConverterTest<UserInvitation> {

    private static final int id = 0;
    private static final int accountId = 1;
    private static final String adminUsername = "test";
    private static final String userEmail = "test@duraspace.org";
    private static final String redemptionCode =
        System.currentTimeMillis() + "";
    private static final Date creationDate = new Date();
    private static final Date expirationDate =
        new Date(creationDate.getTime() + 86400 * 1000 * 10);

    private static final int counter = 4;

    @Override
    protected DomainConverter<UserInvitation> createConverter() {
        return new DuracloudUserInvitationConverter();
    }

    @Override
    protected UserInvitation createTestItem() {
        UserInvitation ui =
            new UserInvitation(
                id, accountId, adminUsername, userEmail, creationDate,
                expirationDate, redemptionCode, counter);

        return ui;
    }

    @Override
    protected List<Attribute> createTestAttributes() {
        BaseDomainConverter c = new BaseDomainConverter() {
        };
        
        List<Attribute> testAtts = new ArrayList<Attribute>();
        testAtts.add(new Attribute(ACCOUNT_ID_ATT, String.valueOf(accountId)));
        testAtts.add(new Attribute(ADMIN_USERNAME_ATT, adminUsername));
        testAtts.add(new Attribute(USER_EMAIL_ATT, userEmail));
        testAtts.add(new Attribute(CREATIONDATE_ATT, c
            .asString(creationDate)));
        testAtts.add(new Attribute(EXPIRATION_DATE_ATT, c
            .asString(expirationDate)));
        testAtts.add(new Attribute(REDEMPTION_CODE_ATT, redemptionCode));
        testAtts.add(new Attribute(COUNTER_ATT, padded(counter)));
        return testAtts;
    }

    @Override
    protected void verifyItem(UserInvitation user) {
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getCounter());
        Assert.assertNotNull(user.getAdminUsername());
        Assert.assertNotNull(user.getUserEmail());
        Assert.assertNotNull(user.getAccountId());
        Assert.assertNotNull(user.getCreationDate());
        Assert.assertNotNull(user.getExpirationDate());
        Assert.assertEquals(counter, user.getCounter());
        Assert.assertEquals(adminUsername, user.getAdminUsername());
        Assert.assertEquals(userEmail, user.getUserEmail());
        Assert.assertEquals(accountId, user.getAccountId());
        Assert.assertEquals(redemptionCode, user.getRedemptionCode());
        Assert.assertEquals(creationDate, user.getCreationDate());
        Assert.assertEquals(expirationDate, user.getExpirationDate());

    }

}
