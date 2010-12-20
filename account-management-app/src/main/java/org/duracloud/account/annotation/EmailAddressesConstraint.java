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
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME) 
@Constraint(validatedBy=EmailAddressesValidator.class)
public @interface EmailAddressesConstraint {
	 	String message() default "The following email addresses are invalid: {0}";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
}

