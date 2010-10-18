/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db;

import java.util.List;

import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;

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
    public T findById(String id) throws DBNotFoundException;

    /**
     * This method stores the arg item to the underlying persistence layer.
     *
     * @param item to be saved
     * @throws DBConcurrentUpdateException if item to be saved is out of date.
     */
    public void save(T item) throws DBConcurrentUpdateException;

    /**
     * This method returns ids for all items in this repo.
     *
     * @return list of ids
     */
    public List<String> getIds();
}
