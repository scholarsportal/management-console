/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.db.DuracloudAccountClusterRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudAccountClusterConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author: Bill Branan
 * Date: 2/16/12
 */
public class DuracloudAccountClusterRepoImpl extends BaseDuracloudRepoImpl
    implements DuracloudAccountClusterRepo {

    private static final String DEFAULT_DOMAIN = "DURACLOUD_ACCOUNT_CLUSTER";

    private final DomainConverter<AccountCluster> converter;

    public DuracloudAccountClusterRepoImpl(
        AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudAccountClusterRepoImpl(
        AmazonSimpleDBClientMgr amazonSimpleDBClientMgr,
        String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log =
            LoggerFactory.getLogger(DuracloudAccountClusterRepoImpl.class);

        this.converter = new DuracloudAccountClusterConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public AccountCluster findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public void save(AccountCluster item) throws DBConcurrentUpdateException {
        doSave(item, converter);
    }
}
