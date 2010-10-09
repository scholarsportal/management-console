/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.simpledb.AmazonSimpleDBAsync;
import com.amazonaws.services.simpledb.model.*;
import com.amazonaws.services.simpledb.util.SimpleDBUtils;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.domain.DuracloudUser;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
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

    private static final String DOMAIN = "DURACLOUD_USERS";
    private static final String USERNAME_ATT = "USERNAME";
    private static final String PASSWORD_ATT = "PASSWORD";
    private static final String FIRSTNAME_ATT = "FIRSTNAME";
    private static final String LASTNAME_ATT = "LASTNAME";
    private static final String EMAIL_ATT = "EMAIL";

    private AmazonSimpleDBAsync db;

    public DuracloudUserRepoImpl(AmazonSimpleDBClientMgr amazonSimpleDBClientMgr) {
        db = amazonSimpleDBClientMgr.getClient();
        createDomainIfNecessary();
    }

    private void createDomainIfNecessary() {
        boolean created = false;

        ListDomainsRequest listRequest = new ListDomainsRequest();
        ListDomainsResult listResult = db.listDomains(listRequest);
        if (null == listResult ||
            !listResult.getDomainNames().contains(DOMAIN)) {
            CreateDomainRequest createRequest = new CreateDomainRequest(DOMAIN);
            db.createDomain(createRequest);

        } else if (listResult.getDomainNames().contains(DOMAIN)) {
            created = true;
        }

        int maxTries = 10;
        int tries = 0;
        while (!created && listResult != null && tries < maxTries) {
            created = listResult.getDomainNames().contains(DOMAIN);
            listResult = db.listDomains(listRequest);
            sleep((long) (Math.random() * (Math.pow(3, tries++) * 10L)));
        }

        if (!created) {
            String msg = "Unable to create domain: " + DOMAIN;
            log.error(msg);
            throw new DuraCloudRuntimeException(msg);
        }
    }

    @Override
    public DuracloudUser findById(String id) throws DBNotFoundException {
        SelectRequest request = new SelectRequest(
            "select * from " + DOMAIN + " where itemName() = '" + id + "'");

        SelectResult result = select(request);
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
        int counter = -1;
        String username = item.getName();
        String password = null;
        String firstname = null;
        String lastname = null;
        String email = null;

        List<Attribute> atts = item.getAttributes();
        for (Attribute att : atts) {
            String name = att.getName();
            String value = att.getValue();
            if (COUNTER_ATT.equals(name)) {
                counter = SimpleDBUtils.decodeZeroPaddingInt(value);

            } else if (USERNAME_ATT.equals(name)) {
                username = value;

            } else if (PASSWORD_ATT.equals(name)) {
                password = value;

            } else if (FIRSTNAME_ATT.equals(name)) {
                firstname = value;

            } else if (LASTNAME_ATT.equals(name)) {
                lastname = value;

            } else if (EMAIL_ATT.equals(name)) {
                email = value;

            } else {
                StringBuilder msg = new StringBuilder("Unexpected name: ");
                msg.append(name);
                msg.append(" in domain: ");
                msg.append(DOMAIN);
                msg.append(" [with id]: ");
                msg.append(id);
                log.info(msg.toString());
            }
        }

        return new DuracloudUser(username,
                                 password,
                                 firstname,
                                 lastname,
                                 email,
                                 counter);
    }

    private String logError(String text, String id) {
        StringBuilder msg = new StringBuilder(text);
        msg.append(" in domain: ");
        msg.append(DOMAIN);
        msg.append(" [with id]: ");
        msg.append(id);
        log.error(msg.toString());
        return msg.toString();
    }

    private SelectResult select(SelectRequest request) {
        try {
            return db.select(request);

        } catch (AmazonServiceException e) {
            throw new DBException(e);
        }
    }

    @Override
    public void save(DuracloudUser item) throws DBConcurrentUpdateException {
        Integer counter = item.getCounter();
        UpdateCondition condition;
        if (0 == counter) {
            condition = new UpdateCondition().withName(COUNTER_ATT).withExists(
                false);
        } else {
            condition = new UpdateCondition(COUNTER_ATT, padded(counter), true);
        }

        List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();
        atts.add(new ReplaceableAttribute(COUNTER_ATT,
                                          padded(counter + 1),
                                          true));
        atts.add(new ReplaceableAttribute(PASSWORD_ATT,
                                          item.getPassword(),
                                          true));
        atts.add(new ReplaceableAttribute(FIRSTNAME_ATT,
                                          item.getFirstName(),
                                          true));
        atts.add(new ReplaceableAttribute(LASTNAME_ATT,
                                          item.getLastName(),
                                          true));
        atts.add(new ReplaceableAttribute(EMAIL_ATT, item.getEmail(), true));

        PutAttributesRequest request = new PutAttributesRequest(DOMAIN,
                                                                item.getUsername(),
                                                                atts,
                                                                condition);

        putAttributes(request);
    }

    private String padded(Integer counter) {
        return SimpleDBUtils.encodeZeroPadding(counter, 10);
    }

    private void putAttributes(PutAttributesRequest request)
        throws DBConcurrentUpdateException {
        try {
            db.putAttributes(request);

        } catch (AmazonServiceException e) {
            if (HttpURLConnection.HTTP_CONFLICT == e.getStatusCode()) {
                throw new DBConcurrentUpdateException(e);
            }
            throw new DBException(e);
        }
    }

    @Override
    public List<String> getIds() {
        String query = "select itemName() from " + DOMAIN;
        SelectRequest request = new SelectRequest(query);
        SelectResult result = select(request);
        if (null == result) {
            StringBuilder msg = new StringBuilder("No users found in domain: ");
            msg.append(DOMAIN);
            log.error(msg.toString());
            throw new DuraCloudRuntimeException(msg.toString());
        }

        List<Item> items = result.getItems();
        if (null == items) {
            StringBuilder msg = new StringBuilder("Items were null");
            msg.append(" in domain: ");
            msg.append(DOMAIN);
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
        DeleteDomainRequest request = new DeleteDomainRequest(DOMAIN);
        db.deleteDomainAsync(request);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // do nothing.
        }
    }

}
