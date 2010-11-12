/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public abstract class PaymentInfo implements Identifiable {

    protected String id;
    protected String streetAddress0;
    protected String streetAddress1;
    protected String streetAddress2;
    protected String city;
    protected String state;
    protected String country;
    protected String postalCode;

    @Override
    public String getId() {
        return id;
    }
}
