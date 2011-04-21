/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudUserInvitationConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class manages the persistence of UserInvitations .
 *
 * @author Daniel Bernstein
 *         Date: Dec 10, 2010
 */
public class DuracloudUserInvitationRepoImpl extends BaseDuracloudRepoImpl implements DuracloudUserInvitationRepo {

    private static final String DEFAULT_DOMAIN = "DURACLOUD_USER_INVITATIONS";

    private final DomainConverter<UserInvitation> converter;

    public DuracloudUserInvitationRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudUserInvitationRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr,
                                 String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log = LoggerFactory.getLogger(DuracloudUserInvitationRepoImpl.class);

        this.converter = new DuracloudUserInvitationConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public UserInvitation findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public UserInvitation findByRedemptionCode(String redemptionCode)
        throws DBNotFoundException {
        Item item = findItemsByAttribute(
                        DuracloudUserInvitationConverter.REDEMPTION_CODE_ATT,
                        redemptionCode).get(0);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public void save(UserInvitation item) throws DBConcurrentUpdateException {
        doSave(item, converter);
    }

    @Override
    public Set<UserInvitation> findByAccountId(int accountId) {
        List<Item> items;
        try {
            items = findItemsByAttribute(
                             DuracloudUserInvitationConverter.ACCOUNT_ID_ATT,
                             String.valueOf(accountId));
        } catch(DBNotFoundException e) {
            items = new ArrayList<Item>(0);
        }

        Set<UserInvitation> invitations = new HashSet<UserInvitation>();
        for(Item item : items) {
            List<Attribute> atts = item.getAttributes();
            invitations.add(
                converter.fromAttributes(atts, idFromString(item.getName())));
        }

        return invitations;
    }

}
