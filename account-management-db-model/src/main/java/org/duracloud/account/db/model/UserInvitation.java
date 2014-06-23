/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Erik Paulsson
 *         Date: 7/11/13
 */
@Entity
public class UserInvitation extends BaseEntity {

    @ManyToOne(fetch= FetchType.EAGER, optional=true)
    @JoinColumn(name="account_id", nullable=true, columnDefinition = "bigint(20)")
    private AccountInfo account;

    private String accountName;
    private String accountOrg;
    private String accountDep;
    private String accountSubdomain;
    private String adminUsername;
    private String userEmail;
    private Date creationDate;
    private Date expirationDate;
    private String redemptionCode;

    public UserInvitation() {}

    public UserInvitation(
            Long id, AccountInfo account, String accountName, String accountOrg, String accountDep,
            String accountSubdomain, String adminUsername, String userEmail,
            int expirationDays, String redemptionCode) {
        this.id = id;
        this.account = account;
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
    }

    public AccountInfo getAccount() {
        return account;
    }

    public void setAccount(AccountInfo account) {
        this.account = account;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountOrg() {
        return accountOrg;
    }

    public void setAccountOrg(String accountOrg) {
        this.accountOrg = accountOrg;
    }

    public String getAccountDep() {
        return accountDep;
    }

    public void setAccountDep(String accountDep) {
        this.accountDep = accountDep;
    }

    public String getAccountSubdomain() {
        return accountSubdomain;
    }

    public void setAccountSubdomain(String accountSubdomain) {
        this.accountSubdomain = accountSubdomain;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getRedemptionCode() {
        return redemptionCode;
    }

    public void setRedemptionCode(String redemptionCode) {
        this.redemptionCode = redemptionCode;
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

    public String getRedemptionURL() {
        return AmaEndpoint.getUrl() + "/users/redeem/" + getRedemptionCode();
    }

    private String getNewProfileURL() {
        return AmaEndpoint.getUrl() + "/users/new";
    }



    @Override
    public String toString() {
        return "UserInvitation[id="
                + id + ", accountId=" + account.getId() + ", accountName=" + accountName
                + ", accountOrg=" + accountOrg + ", accountDep=" + accountDep
                + ", accountSubdomain=" + accountSubdomain + ", adminUsername=" + adminUsername
                + ", userEmail=" + userEmail
                + ", creationDate=" + creationDate + ", expirationDate="
                + expirationDate + ", redemptionCode=" + redemptionCode + "]";

    }
}
