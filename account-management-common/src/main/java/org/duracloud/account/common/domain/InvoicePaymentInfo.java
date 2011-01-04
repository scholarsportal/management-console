/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;



/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class InvoicePaymentInfo extends PaymentInfo {

    private int contactDuracloudId;
    private String name;
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
