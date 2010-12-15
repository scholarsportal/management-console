/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;

import java.util.Set;

/**
 * This interface defines the contract of item repositories.
 *
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public interface BaseRepo<T> {

    public final static String COUNTER_ATT = "COUNTER";

    /**
     * This method returns a single item that has a primary key equal to the
     * arg id.
     *
     * @param id of sought item
     * @return item
     * @throws DBNotFoundException if no item found
     */
    public T findById(int id) throws DBNotFoundException;

    /**
     * This method stores the arg item to the underlying persistence layer.
     *
     * @param item to be saved
     * @throws DBConcurrentUpdateException if item to be saved is out of date.
     */
    public void save(T item) throws DBConcurrentUpdateException;

    /**
     * This method removes an item along with all of its associated attributes
     * from the underlying persistence layer. The item to be removed has a 
     * primary key equal to the arg id.
     *
     * No exception is thrown if the item does not exist.
     *
     * @param id of the item to be removed
     */
    public void delete(int id);

    /**
     * This method returns ids for all items in this repo.
     *
     * @return set of ids
     */
    public Set<Integer> getIds();
}
