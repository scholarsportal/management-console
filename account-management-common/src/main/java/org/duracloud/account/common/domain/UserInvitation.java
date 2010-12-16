/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import java.util.Date;

/**
 * @author: Bill Branan
 * Date: Dec 2, 2010
 */
public class UserInvitation extends BaseDomainData {

    private int accountId;
    private String userEmail;
    private Date creationDate;
    private Date expirationDate;
    private String redemptionCode;

    public UserInvitation(int id,
                          int accountId,
                          String userEmail,
                          int expirationDays,
                          String redemptionCode) {
        this(id, accountId, userEmail, expirationDays, redemptionCode, 0);
    }

    public UserInvitation(int id,
                          int accountId,
                          String userEmail,
                          int expirationDays,
                          String redemptionCode,
                          int counter) {
        this.id = id;
        this.accountId = accountId;
        this.userEmail = userEmail;

        this.creationDate = new Date();

        // milliseconds until expiration (days * millis in a day)
        long expMillis = expirationDays * 86400000;
        this.expirationDate = new Date(creationDate.getTime() + expMillis);

        this.redemptionCode = redemptionCode;
        this.counter = counter;
    }

    public UserInvitation(int id,
                          int accountId,
                          String userEmail,
                          Date creationDate,
                          Date expirationDate,
                          String redemptionCode,
                          int counter) {
        this.id = id;
        this.accountId = accountId;
        this.userEmail = userEmail;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.redemptionCode = redemptionCode;
        this.counter = counter;
    }


    public int getAccountId() {
        return accountId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String getRedemptionCode() {
        return redemptionCode;
    }
}
