/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
@Constraint(validatedBy=UniqueSubdomainValidator.class)
public @interface UniqueSubdomainConstraint {
	 	String message() default "The subdomain is already in use. Please choose another.";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
}
