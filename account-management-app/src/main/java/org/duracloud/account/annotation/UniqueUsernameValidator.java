/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.duracloud.account.util.DuracloudUserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsernameConstraint, String> {
	
	@Autowired
	private DuracloudUserService duracloudUserService;


	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return this.duracloudUserService.isUsernameAvailable(value);
	}



	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(UniqueUsernameConstraint constraintAnnotation) {
		// TODO Auto-generated method stub
		
	}

	public DuracloudUserService getUserService() {
		return duracloudUserService;
	}



	public void setUserService(DuracloudUserService duracloudUserService) {
		this.duracloudUserService = duracloudUserService;
	}
}
