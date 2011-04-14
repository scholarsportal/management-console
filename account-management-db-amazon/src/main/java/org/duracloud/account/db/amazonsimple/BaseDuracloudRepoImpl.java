/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ListDomainsRequest;
import com.amazonaws.services.simpledb.model.ListDomainsResult;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.amazonaws.services.simpledb.model.UpdateCondition;
import org.duracloud.account.common.domain.Identifiable;
import org.duracloud.account.db.error.DBException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.util.FormatUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.duracloud.account.db.BaseRepo.COUNTER_ATT;

/**
 * @author: Bill Branan
 * Date: Dec 1, 2010
 */
public abstract class BaseDuracloudRepoImpl {

    protected Logger log = LoggerFactory.getLogger(BaseDuracloudRepoImpl.class);

    protected AmazonSimpleDBCaller caller;
    protected AmazonSimpleDB db;
    protected String domain;

    public BaseDuracloudRepoImpl(AmazonSimpleDBCaller caller,
                                 AmazonSimpleDB db,
                                 String domain) {
        this.caller = caller;
        this.db = db;
        this.domain = domain;
    }

    public void createDomainIfNecessary() {
        log.debug("creating domain: " + domain);

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

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // do nothing.
        }
    }

    public Item findItemById(int id) throws DBNotFoundException {
        boolean consistent = true;
        SelectRequest request = new SelectRequest(
            "select * from " + domain + " where itemName() = '" + id + "'",
            consistent);

        SelectResult result = caller.select(db, request);
        if (null == result) {
            throw new DBException(
                getErrorMsg("Null result", "id=" + String.valueOf(id)));
        }

        List<Item> items = result.getItems();
        if (null == items || items.size() == 0) {
            throw new DBNotFoundException(
                getErrorMsg("No items found", "id=" + String.valueOf(id)));
        }

        if (items.size() != 1) {
            throw new DBException(
                getErrorMsg("Unexpected item count: " + items.size(),
                            "id=" + String.valueOf(id)));
        }

        return items.get(0);
    }

    public List<Item> findItemsByAttribute(String attName, String attValue)
        throws DBNotFoundException {
        SelectRequest request = new SelectRequest(
            "select * from " + domain + " where " +
                attName + " = '" + attValue + "'");

        SelectResult result = caller.select(db, request);
        if (null == result) {
            throw new DBException(
                getErrorMsg("Null result", attName+"="+attValue));
        }

        List<Item> items = result.getItems();
        if (null == items || items.size() == 0) {
            throw new DBNotFoundException(
                getErrorMsg("No items found", attName+"="+attValue));
        }

        return items;
    }

    /**
     * Gets the first item with the given attributes
     *
     * @param attributes map of attribute names to attribute values
     */
    public Item findItemByAttributes(Map<String, String> attributes)
        throws DBNotFoundException {
        List<String> attSelections = new ArrayList<String>();
        for(String attName : attributes.keySet()) {
            String attValue = attributes.get(attName);
            attSelections.add(attName + " = '" + attValue + "'");
        }

        String selectStatement = "select * from " + domain + " where";
        boolean start = true;
        for(String attSelection : attSelections) {
            if(start) {
                selectStatement += " " + attSelection;
                start = false;
            } else {
                selectStatement += " intersection " + attSelection;
            }
        }

        SelectRequest request = new SelectRequest(selectStatement);

        SelectResult result = caller.select(db, request);
        if (null == result) {
            throw new DBException(getErrorMsg("Null result", selectStatement));
        }

        List<Item> items = result.getItems();
        if (null == items || items.size() == 0) {
            throw new DBNotFoundException(
                getErrorMsg("No items found", selectStatement));
        }

        if (items.size() != 1) {
            throw new DBException(
                getErrorMsg("Unexpected item count: " + items.size(), 
                            selectStatement));
        }

        return items.get(0);
    }

    public void delete(int id) {
        DeleteAttributesRequest request =
            new DeleteAttributesRequest(domain, String.valueOf(id));
        caller.deleteAttributes(db, request);
    }

    private String getErrorMsg(String text, String id) {
        StringBuilder msg = new StringBuilder(text);
        msg.append(" in domain: ");
        msg.append(domain);
        msg.append(" [for]: ");
        msg.append(id);
        return msg.toString();
    }

    protected UpdateCondition getUpdateCondition(Integer counter) {
        UpdateCondition condition;
        if (0 == counter) {
            condition =
                new UpdateCondition().withName(COUNTER_ATT).withExists(false);
        } else {
            condition = new UpdateCondition(COUNTER_ATT,
                                            FormatUtil.padded(counter),
                                            true);
        }
        return condition;
    }    

    public Set<Integer> getItemIds() {
        String query = "select itemName() from " + domain;
        SelectRequest request = new SelectRequest(query);
        SelectResult result = caller.select(db, request);

        Set<Integer> ids = new HashSet<Integer>();
        if (null != result) {
            List<Item> items = result.getItems();
            if (null != items) {   
                for (Item item : items) {
                    String itemName = item.getName();
                    try {
                        ids.add(Integer.valueOf(itemName));
                    } catch(NumberFormatException e) {
                        log.error("Item name " + itemName + " in domain " +
                                  domain + " is not an integer!");
                    }
                }
            }
        }
        return ids;
    }

    protected String idAsString(Identifiable item) {
        return String.valueOf(item.getId());
    }

    protected int idFromString(String origId) {
        try {
            return Integer.valueOf(origId);
        } catch (NumberFormatException e) {
            log.error("Item ID " + origId + " in domain " +
                      domain + " is not a valid integer!");
            return -1;
        }
    }

    public Set<Integer> getIds() {
        return getItemIds();
    }

    /**
     * This method is NOT part of the interface contract.
     */
    public void removeDomain() {
        DeleteDomainRequest request = new DeleteDomainRequest(domain);
        caller.deleteDomainAsync(db, request);
    }

    /**
     * This method returns all rows of the table.
     * @return all items in table
     * @throws DBNotFoundException
     */
    protected List<Item> findAllItems() throws DBNotFoundException {
        SelectRequest request = new SelectRequest("select * from " + domain);

        SelectResult result = caller.select(db, request);
        if (null == result) {
            throw new DBException("Null result: " + domain);
        }

        List<Item> items = result.getItems();
        if (null == items || items.size() == 0) {
            throw new DBNotFoundException("No items found in: " + domain);
        }

        return items;
    }

}
