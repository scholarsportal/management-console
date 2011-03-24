/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudComputeProviderAccountConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author: Bill Branan
 * Date: 3/24/11
 */
public class DuracloudComputeProviderAccountRepoImpl extends BaseDuracloudRepoImpl
    implements DuracloudComputeProviderAccountRepo {

    private static final String DEFAULT_DOMAIN = "DURACLOUD_COMPUTE_PROVIDER_ACCOUNTS";

    private final DomainConverter<ComputeProviderAccount> converter;

    public DuracloudComputeProviderAccountRepoImpl(
        AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudComputeProviderAccountRepoImpl(
        AmazonSimpleDBClientMgr amazonSimpleDBClientMgr, String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log = LoggerFactory
            .getLogger(DuracloudComputeProviderAccountRepoImpl.class);

        this.converter = new DuracloudComputeProviderAccountConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public ComputeProviderAccount findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public void save(ComputeProviderAccount item) throws DBConcurrentUpdateException {
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
