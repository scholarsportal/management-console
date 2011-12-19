
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
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class UsernameValidator implements ConstraintValidator<UsernameConstraint, String> {
	
	private static final String INVALID_USERNAME_MESSAGE =  
	    "The username is invalid. Usernames must contain only lowercase letters, numbers, " +
	    "'-','_','@', '.', and start and end with a letter or number.";

    @Autowired(required=true)
	private DuracloudUserService duracloudUserService;

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
	    try{
	        this.duracloudUserService.checkUsername(value);
	    }catch(InvalidUsernameException ex){
	        context.disableDefaultConstraintViolation();
	        context.buildConstraintViolationWithTemplate(INVALID_USERNAME_MESSAGE)
	                .addConstraintViolation();
	        return false;
	    }catch(UserAlreadyExistsException ex){
	        return false;
	    }
	    
	    return true;
	}


    /* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(UsernameConstraint constraintAnnotation) {

	}

	public DuracloudUserService getUserService() {
		return duracloudUserService;
	}



	public void setUserService(DuracloudUserService duracloudUserService) {
		this.duracloudUserService = duracloudUserService;
	}
}
