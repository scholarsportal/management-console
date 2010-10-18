/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import java.util.List;

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
	@Length(min=3, max=20)
	private String subdomain;
	
	private List<StorageProviderType> storageProviders;


	public String getSubdomain() {
		return subdomain;
	}

	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}

	public List<StorageProviderType> getStorageProviders() {
		return storageProviders;
	}

	public void setStorageProviders(List<StorageProviderType> storageProviders) {
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
