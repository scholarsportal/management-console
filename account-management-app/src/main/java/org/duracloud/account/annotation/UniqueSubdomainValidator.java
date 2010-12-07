/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.duracloud.account.util.AccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class UniqueSubdomainValidator implements ConstraintValidator<UniqueSubdomainConstraint, String> {
	
	@Autowired
	private AccountManagerService accountManagerService;


	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return this.accountManagerService.subdomainAvailable(value);
	}

	public void setAccountManagerService(AccountManagerService accountManagerService) {
		this.accountManagerService = accountManagerService;
	}

	public AccountManagerService getAccountManagerService() {
		return accountManagerService;
	}

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(UniqueSubdomainConstraint constraintAnnotation) {
		// TODO Auto-generated method stub
		
	}
}
