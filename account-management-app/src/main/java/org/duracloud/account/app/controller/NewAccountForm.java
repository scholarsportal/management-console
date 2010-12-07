/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.List;
import java.util.Set;

import org.duracloud.account.annotation.UniqueSubdomainConstraint;
import org.duracloud.storage.domain.StorageProviderType;
import org.hibernate.validator.constraints.Length;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class NewAccountForm {
	
	private String orgName;
	
	private String department;
	
	private String acctName;
	
	@UniqueSubdomainConstraint
	@Length(min=3, max=20, message = "Subdomain must be between 3 and 20 characters.")
	private String subdomain;
	
	private Set<StorageProviderType> storageProviders;


	public String getSubdomain() {
		return subdomain;
	}

	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}

	public Set<StorageProviderType> getStorageProviders() {
		return storageProviders;
	}

	public void setStorageProviders(Set<StorageProviderType> storageProviders) {
		this.storageProviders = storageProviders;
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

	public String getAcctName() {
		return acctName;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}
}
