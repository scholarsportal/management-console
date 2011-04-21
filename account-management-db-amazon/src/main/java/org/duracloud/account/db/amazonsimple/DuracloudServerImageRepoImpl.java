/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudServerImageConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.duracloud.account.db.amazonsimple.converter.DuracloudServerImageConverter.LATEST_ATT;

/**
 * @author: Bill Branan
 * Date: Feb 1, 2011
 */
public class DuracloudServerImageRepoImpl
    extends BaseDuracloudRepoImpl
    implements DuracloudServerImageRepo {

    private static final String DEFAULT_DOMAIN = "DURACLOUD_SERVER_IMAGES";

    private final DomainConverter<ServerImage> converter;

    public DuracloudServerImageRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudServerImageRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr,
                                        String domain) {
        super(new AmazonSimpleDBCaller(),
              amazonSimpleDBClientMgr.getClient(),
              domain);
        this.log = LoggerFactory.getLogger(DuracloudServerImageRepoImpl.class);

        this.converter = new DuracloudServerImageConverter();
        this.converter.setDomain(domain);

        createDomainIfNecessary();
    }

    @Override
    public ServerImage findById(int id) throws DBNotFoundException {
        Item item = findItemById(id);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, idFromString(item.getName()));
    }

    @Override
    public ServerImage findLatest() {
        List<Item> items;
        try {
            items = findItemsByAttribute(LATEST_ATT, String.valueOf(true));
        } catch(DBNotFoundException e) {
            String err = "No server image is marked as latest!";
            throw new DuraCloudRuntimeException(err);
        }

        if(items.size() == 1) {
            Item item = items.iterator().next();
            return converter.fromAttributes(item.getAttributes(),
                                            idFromString(item.getName()));
        } else { // items.size() > 1
            String err = "More than one server image marked as latest " +
                "was found. One of the following needs to be updated:";
            for(Item item : items) {
                err += (" " + item.getName()) ;
            }
            throw new DuraCloudRuntimeException(err);
        }
    }

    @Override
    public void save(ServerImage item) throws DBConcurrentUpdateException {
        doSave(item, converter);
    }

}
