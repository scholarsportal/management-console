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

    public String getStreetAddress0() {
        return streetAddress0;
    }

    public void setStreetAddress0(String streetAddress0) {
        this.streetAddress0 = streetAddress0;
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
