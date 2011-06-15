/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import java.util.Set;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class AccountInfo extends BaseDomainData implements Comparable<AccountInfo> {

    public enum AccountStatus {
        PENDING, ACTIVE;
    }

    public enum PackageType {
        STARTER_ARCHIVING("Starter Package: Archiving and Preservation"),
        STARTER_MEDIA("Starter Package: Media Access"),
        PROFESSIONAL("Professional Package");

        private final String text;

        private PackageType(String pt) {
            text = pt;
        }

        public static PackageType fromString(String pt) {
            for (PackageType pType : values()) {
                if (pType.text.equalsIgnoreCase(pt)||
                    pType.name().equalsIgnoreCase(pt)) {
                    return pType;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return text;
        }

        public String getText() {
            return text;
        }

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

    /**
     * The ID of the ComputeProviderAccount which is the compute provider for
     * an instance
     */
    private int computeProviderAccountId;

    /**
     * The ID of a StorageProviderAccount which is used for primary storage
     */
    private int primaryStorageProviderAccountId;

    /**
     * The IDs of all StorageProviderAccounts which are used for secondary storage
     */
    private Set<Integer> secondaryStorageProviderAccountIds;

    /**
     * The IDs of all ServiceRepositories used to store service binaries
     */
    private Set<Integer> secondaryServiceRepositoryIds;

    /*
     * The ID of the PaymentInfo which tracks the payment details for this
     * account
     */
    private int paymentInfoId;

    /*
     * The package type for this account
     */
    private PackageType packageType;

    private AccountStatus status;

	public AccountInfo(int id,
                       String subdomain,
                       String acctName,
                       String orgName,
			           String department,
                       int computeProviderAccountId,
                       int primaryStorageProviderAccountId,
                       Set<Integer> secondaryStorageProviderAccountIds,
                       Set<Integer> secondaryServiceRepositoryIds,
                       int paymentInfoId,
                       PackageType packageType,
                       AccountStatus status) {
		this(id,
             subdomain,
             acctName,
             orgName,
             department,
             computeProviderAccountId,
             primaryStorageProviderAccountId,
             secondaryStorageProviderAccountIds,
             secondaryServiceRepositoryIds,
             paymentInfoId,
             packageType,
             status,
             0);
	}

    public AccountInfo(int id,
                       String subdomain,
                       String acctName,
                       String orgName,
                       String department,
                       int computeProviderAccountId,
                       int primaryStorageProviderAccountId,
                       Set<Integer> secondaryStorageProviderAccountIds,
                       Set<Integer> secondaryServiceRepositoryIds,
                       int paymentInfoId,
                       PackageType packageType,
                       AccountStatus status,
                       int counter) {
		super();
		this.id = id;
		this.subdomain = subdomain;
		this.acctName = acctName;
		this.orgName = orgName;
		this.department = department;
        this.computeProviderAccountId = computeProviderAccountId;
        this.primaryStorageProviderAccountId = primaryStorageProviderAccountId;
        this.secondaryStorageProviderAccountIds = secondaryStorageProviderAccountIds;
        this.secondaryServiceRepositoryIds = secondaryServiceRepositoryIds;
        this.paymentInfoId = paymentInfoId;
        this.packageType = packageType;
        this.status = status;
        this.counter = counter;
	}

    public String getSubdomain() {
        return subdomain;
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

    public int getComputeProviderAccountId() {
        return computeProviderAccountId;
    }

    public int getPrimaryStorageProviderAccountId() {
        return primaryStorageProviderAccountId;
    }

    public Set<Integer> getSecondaryStorageProviderAccountIds() {
        return secondaryStorageProviderAccountIds;
    }

    public Set<Integer> getSecondaryServiceRepositoryIds() {
        return secondaryServiceRepositoryIds;
    }

    public int getPaymentInfoId() {
        return paymentInfoId;
    }

    public PackageType getPackageType() {
        return packageType;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    @Override
    public int compareTo(AccountInfo o) {
        return this.acctName.compareTo(o.acctName);
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

        AccountInfo that = (AccountInfo) o;

        if (computeProviderAccountId != that.computeProviderAccountId) {
            return false;
        }
        if (paymentInfoId != that.paymentInfoId) {
            return false;
        }
        if (packageType != that.packageType) {
            return false;
        }
        if (primaryStorageProviderAccountId !=
            that.primaryStorageProviderAccountId) {
            return false;
        }
        if (acctName != null ? !acctName.equals(that.acctName) :
            that.acctName != null) {
            return false;
        }
        if (department != null ? !department.equals(that.department) :
            that.department != null) {
            return false;
        }
        if (orgName != null ? !orgName.equals(that.orgName) :
            that.orgName != null) {
            return false;
        }
        if (secondaryServiceRepositoryIds !=
            null ? !secondaryServiceRepositoryIds
            .equals(that.secondaryServiceRepositoryIds) :
            that.secondaryServiceRepositoryIds != null) {
            return false;
        }
        if (secondaryStorageProviderAccountIds !=
            null ? !secondaryStorageProviderAccountIds
            .equals(that.secondaryStorageProviderAccountIds) :
            that.secondaryStorageProviderAccountIds != null) {
            return false;
        }
        if (status != that.status) {
            return false;
        }
        if (subdomain != null ? !subdomain.equals(that.subdomain) :
            that.subdomain != null) {
            return false;
        }

        return true;
    }
    
    /*
     * Generated by IntelliJ
     */
    @Override
    public int hashCode() {
        int result = subdomain != null ? subdomain.hashCode() : 0;
        result = 31 * result + (acctName != null ? acctName.hashCode() : 0);
        result = 31 * result + (orgName != null ? orgName.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        result = 31 * result + computeProviderAccountId;
        result = 31 * result + primaryStorageProviderAccountId;
        result = 31 * result + (secondaryStorageProviderAccountIds !=
            null ? secondaryStorageProviderAccountIds.hashCode() : 0);
        result = 31 * result + (secondaryServiceRepositoryIds !=
            null ? secondaryServiceRepositoryIds.hashCode() : 0);
        result = 31 * result + paymentInfoId;
        result = 31 * result + (packageType != null ? packageType.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }    

}