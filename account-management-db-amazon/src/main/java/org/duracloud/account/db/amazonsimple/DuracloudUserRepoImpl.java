/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter.USERNAME_ATT;

/**
 * This class manages the persistence of DuracloudUsers.
 *
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public class DuracloudUserRepoImpl extends BaseDuracloudRepoImpl implements DuracloudUserRepo {

    private static final String DEFAULT_DOMAIN = "DURACLOUD_USERS";

    private final DomainConverter<DuracloudUser> converter;

    public DuracloudUserRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudUserRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr,
                                 String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log = LoggerFactory.getLogger(DuracloudUserRepoImpl.class);

        this.converter = new DuracloudUserConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public DuracloudUser findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public DuracloudUser findByUsername(String username) throws DBNotFoundException {
        Item item = findItemsByAttribute(USERNAME_ATT, username).get(0);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public void save(DuracloudUser item) throws DBConcurrentUpdateException {
        doSave(item, converter);
    }

}
