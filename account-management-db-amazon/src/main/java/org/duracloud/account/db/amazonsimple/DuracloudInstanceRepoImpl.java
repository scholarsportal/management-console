/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.duracloud.account.db.amazonsimple.converter.DuracloudInstanceConverter.ACCOUNT_ID_ATT;

/**
 * @author: Bill Branan
 * Date: Dec 20, 2010
 */
public class DuracloudInstanceRepoImpl
    extends BaseDuracloudRepoImpl
    implements DuracloudInstanceRepo {

    private static final String DEFAULT_DOMAIN = "DURACLOUD_INSTANCES";

    private final DomainConverter<DuracloudInstance> converter;

    public DuracloudInstanceRepoImpl(
        AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudInstanceRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr,
                                    String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log = LoggerFactory.getLogger(DuracloudInstanceRepoImpl.class);

        this.converter = new DuracloudInstanceConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public DuracloudInstance findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public Set<Integer> findByAccountId(int accountId)
        throws DBNotFoundException {
        List<Item> items =
            findItemsByAttribute(ACCOUNT_ID_ATT, String.valueOf(accountId));
        return getInstanceIdsFromItems(items);
    }

    private Set<Integer> getInstanceIdsFromItems(List<Item> items) {
        Set<Integer> instanceIds = new HashSet<Integer>();
        for(Item item : items) {
            instanceIds.add(idFromString(item.getName()));
        }
        return instanceIds;
    }

    @Override
    public void save(DuracloudInstance item) throws DBConcurrentUpdateException {
        UpdateCondition condition = getUpdateCondition(item.getCounter());

        List<ReplaceableAttribute> atts =
            converter.toAttributesAndIncrement(item);
        PutAttributesRequest request = new PutAttributesRequest(domain,
                                                                idAsString(item),
                                                                atts,
                                                                condition);
        caller.putAttributes(db, request);
    }

}