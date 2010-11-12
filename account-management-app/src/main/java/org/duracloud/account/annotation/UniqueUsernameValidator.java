/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.util.DuracloudUserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsernameConstraint, String> {
	
	@Autowired(required=true)
	private DuracloudUserService duracloudUserService;

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return StringUtils.isBlank(value) ||  this.duracloudUserService.isUsernameAvailable(value);
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
