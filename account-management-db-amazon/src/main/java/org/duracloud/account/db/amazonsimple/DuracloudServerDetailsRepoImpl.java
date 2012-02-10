/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.db.DuracloudServerDetailsRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudServerDetailsConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author: Bill Branan
 * Date: 2/8/12
 */
public class DuracloudServerDetailsRepoImpl extends BaseDuracloudRepoImpl
    implements DuracloudServerDetailsRepo {

    private static final String DEFAULT_DOMAIN = "DURACLOUD_SERVER_DETAILS";

    private final DomainConverter<ServerDetails> converter;

    public DuracloudServerDetailsRepoImpl(
        AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudServerDetailsRepoImpl(
        AmazonSimpleDBClientMgr amazonSimpleDBClientMgr,
        String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log = LoggerFactory.getLogger(DuracloudServerDetailsRepoImpl.class);

        this.converter = new DuracloudServerDetailsConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public ServerDetails findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public void save(ServerDetails item) throws DBConcurrentUpdateException {
        doSave(item, converter);
    }

}