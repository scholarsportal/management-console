/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author Daniel Bernstein
 *         Date: Feb 8, 2012
 */
@Target({ElementType.METHOD, ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME) 
@Constraint(validatedBy=UsernameExistsValidator.class)
public @interface UsernameExists {
	 	String message() default 
	 	        "The username you specified does not exist in our system. " +
	 			"Please make sure you entered it correctly.";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
}

