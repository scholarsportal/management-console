/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public abstract class PaymentInfo extends BaseDomainData {

    private String streetAddress0;
    private String streetAddress1;
    private String streetAddress2;
    private String city;
    private String state;
    private String country;
    private String postalCode;

}
