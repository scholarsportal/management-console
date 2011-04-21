/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.duracloud.account.db.amazonsimple.converter.DuracloudAccountConverter.SUBDOMAIN_ATT;

/**
 * @author Andrew Woods
 *         Date: Oct 10, 2010
 */
public class DuracloudAccountRepoImpl extends BaseDuracloudRepoImpl implements DuracloudAccountRepo {

    private static final String DEFAULT_DOMAIN = "DURACLOUD_ACCOUNTS";

    private final DomainConverter<AccountInfo> converter;

    public DuracloudAccountRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudAccountRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr,
                                    String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log = LoggerFactory.getLogger(DuracloudAccountRepoImpl.class);

        this.converter = new DuracloudAccountConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public AccountInfo findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public void save(AccountInfo item) throws DBConcurrentUpdateException {
        doSave(item, converter);
    }

    @Override
    public AccountInfo findBySubdomain(String subdomain)
        throws DBNotFoundException {
        List<Item> items = findItemsByAttribute(SUBDOMAIN_ATT, subdomain);
        Item item = items.iterator().next(); // Only one result expected
        return converter.fromAttributes(item.getAttributes(),
                                        idFromString(item.getName()));
    }

}
