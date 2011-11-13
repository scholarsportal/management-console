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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public DuracloudGroup findByGroupname(String groupname)
        throws DBNotFoundException {
        Item item = findItemsByAttribute(GROUPNAME_ATT, groupname).get(0);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public Set<DuracloudGroup> findAllGroups() throws DBNotFoundException {
        Set<DuracloudGroup> groups = new HashSet<DuracloudGroup>();

        List<Item> items = findAllItems();
        for (Item item : items) {
            groups.add(converter.fromAttributes(item.getAttributes(),
                                                idFromString(item.getName())));
        }
        return groups;
    }

    @Override
    public void save(DuracloudGroup item) throws DBConcurrentUpdateException {
        doSave(item, converter);
    }

}
