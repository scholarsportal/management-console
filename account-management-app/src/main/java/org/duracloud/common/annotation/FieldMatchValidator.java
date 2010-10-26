/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.common.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@durspace.org)"
 * 
 */
public class FieldMatchValidator implements
		ConstraintValidator<FieldMatch, Object> {
	private String firstFieldName;
	private String secondFieldName;

	@Override
	public void initialize(final FieldMatch constraintAnnotation) {
		firstFieldName = constraintAnnotation.first();
		secondFieldName = constraintAnnotation.second();
	}

	@Override
	public boolean isValid(final Object value,
			final ConstraintValidatorContext context) {
		try {
			
			final Object firstObj = BeanUtils
					.getProperty(value, firstFieldName);
			final Object secondObj = BeanUtils.getProperty(value,
					secondFieldName);

			return firstObj == null && secondObj == null || firstObj != null
					&& firstObj.equals(secondObj);
		} catch (final Exception ignore) {
			// ignore
		}
		return true;
	}
}
