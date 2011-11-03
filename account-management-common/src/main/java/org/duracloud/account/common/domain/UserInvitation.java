/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import java.util.Date;

/**
 * @author: Bill Branan Date: Dec 2, 2010
 */
public class UserInvitation extends BaseDomainData implements Comparable<UserInvitation> {

    private int accountId;
    private String accountName;
    private String accountOrg;
    private String accountDep;
    private String accountSubdomain;
    private String adminUsername;
    private String userEmail;
    private Date creationDate;
    private Date expirationDate;
    private String redemptionCode;

    public UserInvitation(
        int id, int accountId, String adminUsername, String userEmail, int expirationDays,
        String redemptionCode) {
        this(id, accountId, adminUsername, userEmail, expirationDays, redemptionCode, 0);
    }

    public UserInvitation(
        int id, int accountId, String accountName, String accountOrg, String accountDep,
        String accountSubdomain, String adminUsername, String userEmail, int expirationDays,
        String redemptionCode) {
        this(id, accountId, accountName, accountOrg, accountDep, accountSubdomain,
             adminUsername, userEmail, expirationDays, redemptionCode, 0);
    }

    public UserInvitation(
        int id, int accountId, String adminUsername, String userEmail, int expirationDays,
        String redemptionCode, int counter) {
        this(id, accountId, null, null, null, null, adminUsername, userEmail, expirationDays, redemptionCode, counter);
    }

    public UserInvitation(
        int id, int accountId, String accountName, String accountOrg, String accountDep,
        String accountSubdomain, String adminUsername, String userEmail,
        int expirationDays, String redemptionCode, int counter) {
        this.id = id;
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountOrg = accountOrg;
        this.accountDep = accountDep;
        this.accountSubdomain = accountSubdomain;
        this.adminUsername = adminUsername;
        this.userEmail = userEmail;

        this.creationDate = new Date();

        // milliseconds until expiration (days * millis in a day)
        long expMillis = expirationDays * 86400000;
        this.expirationDate = new Date(creationDate.getTime() + expMillis);

        this.redemptionCode = redemptionCode;
        this.counter = counter;
    }

    public UserInvitation(
        int id, int accountId, String adminUsername, String userEmail, Date creationDate,
        Date expirationDate, String redemptionCode, int counter) {
        this.id = id;
        this.accountId = accountId;
        this.adminUsername = adminUsername;
        this.userEmail = userEmail;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.redemptionCode = redemptionCode;
        this.counter = counter;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountOrg() {
        return accountOrg;
    }

    public String getAccountDep() {
        return accountDep;
    }

    public String getAccountSubdomain() {
        return accountSubdomain;
    }

    public String getAdminUsername() {
        return adminUsername;
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

    public String getRedemptionURL() {
        return AmaEndpoint.getUrl() + "/users/redeem/" + getRedemptionCode();
    }

    private String getNewProfileURL() {
        return AmaEndpoint.getUrl() + "/users/new";
    }

    public String getSubject() {
        return "DuraCloud Account Invitation";
    }

    public String getBody() {
        StringBuilder sb = new StringBuilder();

        sb.append("You have been invited to join the DuraCloud account which is managed by ");
        sb.append(getAccountOrg());
        if(getAccountDep() != null && !getAccountDep().equals("")) {
            sb.append(", ");
            sb.append(getAccountDep());
        }
        sb.append(". After accepting this invitation, you will be given access to the ");
        sb.append(getAccountName());
        sb.append(" account and will be able to log in to https://");
        sb.append(getAccountSubdomain());
        sb.append(".duracloud.org. In order to join, please follow these instructions:");
        sb.append("\n\n");
        sb.append("1. If you do not have a personal user profile with ");
        sb.append("DuraCloud, please create one at: ");
        sb.append(getNewProfileURL());
        sb.append(" ");
        sb.append("If you already have a personal user profile with ");
        sb.append("DuraCloud, you may skip this step.");
        sb.append("\n");
        sb.append("2. Click on this link to log in to the DuraCloud ");
        sb.append("Management Console: ");
        sb.append(getRedemptionURL());
        sb.append("\n");
        sb.append("3. After logging in, you should see the details for the ");
        sb.append("DuraCloud account that you now have access to.");
        sb.append("\n\n");
        sb.append("Please note that the links above are not supported by ");
        sb.append("Internet Explorer version 8 or prior. It is recommended ");
        sb.append("that you use either Internet Explorer version 9 (or ");
        sb.append("later) or another web browser to access DuraCloud.");
        sb.append("\n\n");
        sb.append("If you have any issues accepting the invitation or ");
        sb.append("creating your personal user profile, please contact the ");
        sb.append("DuraCloud team by emailing your issue to ");
        sb.append("support@duracloud.org.");
        sb.append("\n");
        sb.append("\n");
        sb.append("The DuraCloud team");

        return sb.toString();
    }

    @Override
    public int compareTo(UserInvitation invitation) {
        if(this.id == invitation.id) {
            if(this.equals(invitation)) {
               return 0;
            } else {
                return this.getUserEmail().compareTo(invitation.getUserEmail());
            }
        } else if(this.id > invitation.id) {
            return 1;
        } else {
            return -1;
        }
    }

    /*
     * Generated by IntelliJ
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserInvitation that = (UserInvitation) o;

        if (accountId != that.accountId) {
            return false;
        }
        if (accountName != that.accountName) {
            return false;
        }
        if (accountOrg != that.accountOrg) {
            return false;
        }
        if (accountDep != that.accountDep) {
            return false;
        }
        if (accountSubdomain != that.accountSubdomain) {
            return false;
        }
        if (adminUsername != null ? !adminUsername.equals(that.adminUsername) :
            that.adminUsername != null) {
            return false;
        }
        if (creationDate != null ? !creationDate.equals(that.creationDate) :
            that.creationDate != null) {
            return false;
        }
        if (expirationDate != null ? !expirationDate
            .equals(that.expirationDate) : that.expirationDate != null) {
            return false;
        }
        if (redemptionCode != null ? !redemptionCode
            .equals(that.redemptionCode) : that.redemptionCode != null) {
            return false;
        }
        if (userEmail != null ? !userEmail.equals(that.userEmail) :
            that.userEmail != null) {
            return false;
        }

        return true;
    }

    /*
     * Generated by IntelliJ
     */
    @Override
    public int hashCode() {
        int result = accountId;
        result = 31 * result + (accountName != null ? accountName.hashCode() : 0);
        result = 31 * result + (accountOrg != null ? accountOrg.hashCode() : 0);
        result = 31 * result + (accountDep != null ? accountDep.hashCode() : 0);
        result = 31 * result + (accountSubdomain != null ? accountSubdomain.hashCode() : 0);
        result = 31 * result + (adminUsername != null ? adminUsername.hashCode() : 0);
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        result =
            31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result +
            (expirationDate != null ? expirationDate.hashCode() : 0);
        result = 31 * result +
            (redemptionCode != null ? redemptionCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserInvitation[id="
            + id + ", accountId=" + accountId + ", accountName=" + accountName
            + ", accountOrg=" + accountOrg + ", accountDep=" + accountDep
            + ", accountSubdomain=" + accountSubdomain + ", adminUsername=" + adminUsername
            + ", userEmail=" + userEmail
            + ", creationDate=" + creationDate + ", expirationDate="
            + expirationDate + ", redemptionCode=" + redemptionCode
            + ", counter=" + counter + "]";

    }
}
