/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.AmazonSimpleDBAsync;
import com.amazonaws.services.simpledb.model.*;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.amazonsimple.converter.DomainConverter;
import org.duracloud.account.db.amazonsimple.converter.DuracloudUserConverter;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.util.FormatUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the persistence of DuracloudUsers.
 *
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public class DuracloudUserRepoImpl implements DuracloudUserRepo {

    private final Logger log = LoggerFactory.getLogger(DuracloudUserRepoImpl.class);

    private static final String DEFAULT_DOMAIN = "DURACLOUD_USERS";

    private final AmazonSimpleDBAsync db;
    private final AmazonSimpleDBCaller caller;
    private final DomainConverter<DuracloudUser> converter;
    private final String domain;

    public DuracloudUserRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        this(amazonSimpleDBClientMgr, DEFAULT_DOMAIN);
    }

    public DuracloudUserRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr,
                                 String domain) {
        this.db = amazonSimpleDBClientMgr.getClient();
        this.domain = domain;

        this.caller = new AmazonSimpleDBCaller();
        this.converter = new DuracloudUserConverter();
        this.converter.setDomain(domain);
        createDomainIfNecessary();
    }

    private void createDomainIfNecessary() {
        boolean created = false;

        ListDomainsRequest listRequest = new ListDomainsRequest();
        ListDomainsResult listResult = caller.listDomains(db, listRequest);
        if (null == listResult ||
            !listResult.getDomainNames().contains(domain)) {
            CreateDomainRequest createRequest = new CreateDomainRequest(domain);
            caller.createDomain(db, createRequest);

        } else if (listResult.getDomainNames().contains(domain)) {
            created = true;
        }

        int maxTries = 10;
        int tries = 0;
        while (!created && listResult != null && tries < maxTries) {
            created = listResult.getDomainNames().contains(domain);
            listResult = caller.listDomains(db, listRequest);
            sleep((long) (Math.random() * (Math.pow(3, tries++) * 10L)));
        }

        if (!created) {
            String msg = "Unable to create domain: " + domain;
            log.error(msg);
            throw new DuraCloudRuntimeException(msg);
        }
    }

    @Override
    public DuracloudUser findById(String id) throws DBNotFoundException {
        SelectRequest request = new SelectRequest(
            "select * from " + domain + " where itemName() = '" + id + "'");

        SelectResult result = caller.select(db, request);
        if (null == result) {
            throw new DBException(logError("Null result", id));
        }

        List<Item> items = result.getItems();
        if (null == items || items.size() == 0) {
            throw new DBNotFoundException(logError("No items found", id));
        }

        if (items.size() != 1) {
            throw new DBException(logError(
                "Unexpected item count: " + items.size(), id));
        }

        Item item = items.get(0);
        List<Attribute> atts = item.getAttributes();
        return converter.fromAttributes(atts, item.getName());
    }

    private String logError(String text, String id) {
        StringBuilder msg = new StringBuilder(text);
        msg.append(" in domain: ");
        msg.append(domain);
        msg.append(" [with id]: ");
        msg.append(id);
        log.error(msg.toString());
        return msg.toString();
    }

    @Override
    public void save(DuracloudUser item) throws DBConcurrentUpdateException {
        Integer counter = item.getCounter();
        UpdateCondition condition;
        if (0 == counter) {
            condition = new UpdateCondition().withName(COUNTER_ATT).withExists(
                false);
        } else {
            condition = new UpdateCondition(COUNTER_ATT, FormatUtil.padded(
                counter), true);
        }

        List<ReplaceableAttribute> atts = converter.toAttributesAndIncrement(
            item);
        PutAttributesRequest request = new PutAttributesRequest(domain,
                                                                item.getUsername(),
                                                                atts,
                                                                condition);

        caller.putAttributes(db, request);
    }

    @Override
    public List<String> getIds() {
        String query = "select itemName() from " + domain;
        SelectRequest request = new SelectRequest(query);
        SelectResult result = caller.select(db, request);
        if (null == result) {
            StringBuilder msg = new StringBuilder("No users found in domain: ");
            msg.append(domain);
            log.error(msg.toString());
            throw new DuraCloudRuntimeException(msg.toString());
        }

        List<Item> items = result.getItems();
        if (null == items) {
            StringBuilder msg = new StringBuilder("Items were null");
            msg.append(" in domain: ");
            msg.append(domain);
            log.error(msg.toString());
            throw new DuraCloudRuntimeException(msg.toString());
        }

        List<String> ids = new ArrayList<String>();
        for (Item item : items) {
            ids.add(item.getName());
        }

        return ids;
    }

    /**
     * This method is NOT part of the DuracloudUserRepo interface contract.
     */
    public void removeDomain() {
        DeleteDomainRequest request = new DeleteDomainRequest(domain);
        caller.deleteDomainAsync(db, request);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // do nothing.
        }
    }

}
