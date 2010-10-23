/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.common.domain;

import java.util.List;

import org.duracloud.storage.domain.StorageProviderType;

/**
 * Read-only summary of salient account details.
 *
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class AccountInfo implements Identifiable {
    private String id;
    private String subdomain;
    private String acctName;
    private String orgName;
    private String department;
    private PaymentInfo paymentInfo;
    private DuracloudUser owner;
    private List<StorageProviderType> storageProviders;

	public AccountInfo(String subdomain, DuracloudUser owner,
			List<StorageProviderType> storageProviders) {
		this(null, subdomain, null, null, null, owner, storageProviders);
	}
	public AccountInfo(String subdomain, String acctName, String orgName,  
			String department, DuracloudUser owner,
			List<StorageProviderType> storageProviders) {
		this(null, subdomain, acctName, orgName, department, owner, storageProviders);
	}
	
	public AccountInfo(String id, String subdomain, String acctName, String orgName,  
			String department, DuracloudUser owner,
			List<StorageProviderType> storageProviders) {
		super();
		this.id = id;
		this.subdomain = subdomain;
		this.acctName = acctName;
		this.orgName = orgName;
		this.department = department;
		this.owner = owner;
		this.storageProviders = storageProviders;
	}

	public String getSubdomain() {
		return subdomain;
	}


	public String getAcctName() {
		return acctName;
	}

	public String getOrgName() {
		return orgName;
	}


	public String getDepartment() {
		return department;
	}

	public PaymentInfo getPaymentInfo() {
		return paymentInfo;
	}

	public DuracloudUser getOwner() {
		return owner;
	}

	public List<StorageProviderType> getStorageProviders() {
		return storageProviders;
	}

    @Override
    public String getId() {
        return id;
    }
}
