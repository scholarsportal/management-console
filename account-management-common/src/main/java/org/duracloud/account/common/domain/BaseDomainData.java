/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

/**
 * @author: Bill Branan
 * Date: Dec 2, 2010
 */
public abstract class BaseDomainData implements Identifiable {

    protected int id;
    protected int counter;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
