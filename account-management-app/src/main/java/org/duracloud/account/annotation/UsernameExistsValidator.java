
/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.InvalidUsernameException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Daniel Bernstein
 *         Date: Feb 8, 2012
 */

public class UsernameExistsValidator implements ConstraintValidator<UsernameExists, String> {
	
    @Autowired(required=true)
	private DuracloudUserService duracloudUserService;

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
	    try{
	        this.duracloudUserService.checkUsername(value);
	        return false;
	    }catch(InvalidUsernameException ex){
            return false;
	    }catch(UserAlreadyExistsException ex){
	        return true;
	    }
	}

    /* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(UsernameExists constraintAnnotation) {

	}

	public DuracloudUserService getUserService() {
		return duracloudUserService;
	}



	public void setUserService(DuracloudUserService duracloudUserService) {
		this.duracloudUserService = duracloudUserService;
	}


}
