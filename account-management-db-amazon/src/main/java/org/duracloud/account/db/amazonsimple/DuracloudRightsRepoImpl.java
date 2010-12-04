/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter.ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudRightsConverter.USER_ID_ATT;

/**
 * @author: Bill Branan
 * Date: Dec 3, 2010
 */
public class DuracloudRightsRepoImpl extends BaseDuracloudRepoImpl implements DuracloudRightsRepo {

    private static final String DEFAULT_DOMAIN = "DURACLOUD_RIGHTS";

    private final DomainConverter<AccountRights> converter;

    public DuracloudRightsRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudRightsRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr,
                                 String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log = LoggerFactory.getLogger(DuracloudUserRepoImpl.class);

        this.converter = new DuracloudRightsConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public AccountRights findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public void save(AccountRights item) throws DBConcurrentUpdateException {
        UpdateCondition condition = getUpdateCondition(item.getCounter());

        List<ReplaceableAttribute> atts =
            converter.toAttributesAndIncrement(item);
        PutAttributesRequest request = new PutAttributesRequest(domain,
                                                                idAsString(item),
                                                                atts,
                                                                condition);
        caller.putAttributes(db, request);
    }

    @Override
    public Set<AccountRights> findByUserId(int userId) throws DBNotFoundException {
        List<Item> items =
            findItemsByAttribute(USER_ID_ATT, String.valueOf(userId));
        return getAccountRightsFromItems(items);
    }

    @Override
    public Set<AccountRights> findByAccountId(int accountId) throws DBNotFoundException {
        List<Item> items =
            findItemsByAttribute(ACCOUNT_ID_ATT, String.valueOf(accountId));
        return getAccountRightsFromItems(items);
    }

    @Override
    public AccountRights findByAccountIdAndUserId(int accountId,
                                                  int userId) throws DBNotFoundException {
        Map attributes = new HashMap<String, String>();
        attributes.put(ACCOUNT_ID_ATT, String.valueOf(accountId));
        attributes.put(USER_ID_ATT, String.valueOf(userId));
        Item item = findItemByAttributes(attributes);

        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    private Set<AccountRights> getAccountRightsFromItems(List<Item> items) {
        Set<AccountRights> set = new HashSet<AccountRights>();
        for(Item item : items) {
            List<Attribute> atts = item.getAttributes();
            set.add(converter.fromAttributes(atts,
                                             idFromString(item.getName())));
        }
        return set;
    }

}
