
/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.annotation;

import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.InvalidUsernameException;
import org.duracloud.account.util.error.ReservedPrefixException;
import org.duracloud.account.util.error.ReservedUsernameException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import java.text.MessageFormat;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class UsernameValidator implements ConstraintValidator<UsernameConstraint, String> {
	
	private static final String INVALID_USERNAME_MESSAGE =  
	    "The username \"{0}\" is invalid. Usernames must contain only " +
        "lowercase letters, numbers, '-','_','@', '.', and start and end " +
        "with a letter or number.";
	private static final String RESERVED_USERNAME_MESSAGE =
        "\"{0}\" is a reserved name. Please choose another username.";
    private static final String RESERVED_PREFIX_MESSAGE =
        "Usernames may not be prefixed by \"" + DuracloudGroup.PREFIX +
        "\". Please choose another username.";

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
            String message;
            if (ex instanceof ReservedUsernameException) {
                message = RESERVED_USERNAME_MESSAGE;
            } else if (ex instanceof ReservedPrefixException) {
                message = RESERVED_PREFIX_MESSAGE;
            } else {
                message = INVALID_USERNAME_MESSAGE;
            }

            message = MessageFormat.format(message, value);
            ConstraintViolationBuilder cvb =
                context.buildConstraintViolationWithTemplate(message);
            cvb.addConstraintViolation();
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
