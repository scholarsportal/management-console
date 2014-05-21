/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.model;

import javax.persistence.*;

/**
 * @author Erik Paulsson
 *         Date: 7/10/13
 */
@Entity
public class AccountInfo extends BaseEntity implements Comparable<AccountInfo> {
    public enum AccountStatus {
        PENDING, ACTIVE, INACTIVE, CANCELLED;
    }

    /*
     * The subdomain of duracloud.org which will be used to access the instance
     * associated with this account
     */
    private String subdomain;

    /*
     * The display name of the account
     */
    private String acctName;

    /*
     * The name of the organization responsible for the content in this account
     */
    private String orgName;

    /*
     * The name of the department (if applicable) of the organization
     * responsible for the content in this account
     */
    private String department;

    /*
     * The current status of this account
     */
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    /*
     * The type of account
     */
    @Enumerated(EnumType.STRING)
    private AccountType type;

    /*
     * The details needed to manage servers associated with this
     * account
     */
    @OneToOne(fetch=FetchType.EAGER, optional=true)
    @JoinColumn(name="server_details_id", nullable=true)
    private ServerDetails serverDetails;

    @ManyToOne(fetch= FetchType.EAGER, optional=true)
    @JoinColumn(name="account_cluster_id", nullable=true)
    private AccountCluster accountCluster;

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public ServerDetails getServerDetails() {
        return serverDetails;
    }

    public void setServerDetails(ServerDetails serverDetails) {
        this.serverDetails = serverDetails;
    }

    public AccountCluster getAccountCluster() {
        return accountCluster;
    }

    public void setAccountCluster(AccountCluster accountCluster) {
        this.accountCluster = accountCluster;
    }

    @Override
    public int compareTo(AccountInfo o) {
        return this.acctName.compareTo(o.acctName);
    }
}
