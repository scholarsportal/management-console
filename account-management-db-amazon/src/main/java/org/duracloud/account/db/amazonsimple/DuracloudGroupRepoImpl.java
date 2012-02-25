/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.db.DuracloudGroupRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudGroupConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.duracloud.account.db.amazonsimple.converter.DuracloudGroupConverter.ACCOUNT_ID_ATT;
import static org.duracloud.account.db.amazonsimple.converter.DuracloudGroupConverter.GROUPNAME_ATT;

/**
 * This class manages the persistence of DuracloudGroups.
 *
 * @author Andrew Woods
 *         Date: Nov 12, 2011
 */
public class DuracloudGroupRepoImpl extends BaseDuracloudRepoImpl implements DuracloudGroupRepo {

    private static final String DEFAULT_DOMAIN = "DURACLOUD_GROUPS";

    private final DomainConverter<DuracloudGroup> converter;

    public DuracloudGroupRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudGroupRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr,
                                  String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log = LoggerFactory.getLogger(DuracloudGroupRepoImpl.class);

        this.converter = new DuracloudGroupConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public DuracloudGroup findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public DuracloudGroup findInAccountByGroupname(String groupname, int acctId)
        throws DBNotFoundException {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(GROUPNAME_ATT, groupname);
        attributes.put(ACCOUNT_ID_ATT, String.valueOf(acctId));
        Item item = findItemByAttributes(attributes);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public Set<DuracloudGroup> findByAccountId(int acctId) {
        List<Item> items;
        try {
            items = findItemsByAttribute(ACCOUNT_ID_ATT,
                                         String.valueOf(acctId));
        } catch(DBNotFoundException e) {
            items = new ArrayList<Item>(0);
        }
        return convertToGroups(items);
    }

    private Set<DuracloudGroup> convertToGroups(List<Item> items) {
        Set<DuracloudGroup> groups = new HashSet<DuracloudGroup>();
        for(Item item : items) {
            groups.add(converter.fromAttributes(item.getAttributes(),
                                                idFromString(item.getName())));
        }
        return groups;
    }

    @Override
    public Set<DuracloudGroup> findAllGroups() {
        List<Item> items;
        try {
            items = findAllItems();
        } catch(DBNotFoundException e) {
            items = new ArrayList<Item>(0);
        }
        return convertToGroups(items);
    }

    @Override
    public void save(DuracloudGroup item) throws DBConcurrentUpdateException {
        doSave(item, converter);
    }

}
