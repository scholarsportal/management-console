/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.db.DuracloudServiceRepositoryRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudServiceRepositoryConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author: Bill Branan
 * Date: Feb 2, 2011
 */
public class DuracloudServiceRepositoryRepoImpl
    extends BaseDuracloudRepoImpl
    implements DuracloudServiceRepositoryRepo {

    private static final String DEFAULT_DOMAIN =
        "DURACLOUD_SERVICE_REPOSITORIES";

    private final DomainConverter<ServiceRepository> converter;

    public DuracloudServiceRepositoryRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudServiceRepositoryRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr,
                                        String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log = LoggerFactory.getLogger(DuracloudServiceRepositoryRepoImpl.class);

        this.converter = new DuracloudServiceRepositoryConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public ServiceRepository findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public void save(ServiceRepository item) throws DBConcurrentUpdateException {
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
