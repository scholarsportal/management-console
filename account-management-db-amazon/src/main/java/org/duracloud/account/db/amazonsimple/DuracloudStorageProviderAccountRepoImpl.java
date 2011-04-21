/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudStorageProviderAccountConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author: Bill Branan
 * Date: Feb 1, 2011
 */
public class DuracloudStorageProviderAccountRepoImpl extends BaseDuracloudRepoImpl
    implements DuracloudStorageProviderAccountRepo {

    private static final String DEFAULT_DOMAIN = "DURACLOUD_STORAGE_PROVIDER_ACCOUNTS";

    private final DomainConverter<StorageProviderAccount> converter;

    public DuracloudStorageProviderAccountRepoImpl(
        AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudStorageProviderAccountRepoImpl(
        AmazonSimpleDBClientMgr amazonSimpleDBClientMgr, String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log = LoggerFactory.getLogger(DuracloudStorageProviderAccountRepoImpl.class);

        this.converter = new DuracloudStorageProviderAccountConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public StorageProviderAccount findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public void save(StorageProviderAccount item)
        throws DBConcurrentUpdateException {
        doSave(item, converter);
    }

}
