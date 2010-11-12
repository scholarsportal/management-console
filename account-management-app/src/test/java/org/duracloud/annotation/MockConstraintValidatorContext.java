/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.annotation;

import javax.validation.ConstraintValidatorContext;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class MockConstraintValidatorContext implements
		ConstraintValidatorContext {

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidatorContext#buildConstraintViolationWithTemplate(java.lang.String)
	 */
	@Override
	public ConstraintViolationBuilder buildConstraintViolationWithTemplate(
			String messageTemplate) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidatorContext#disableDefaultConstraintViolation()
	 */
	@Override
	public void disableDefaultConstraintViolation() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidatorContext#getDefaultConstraintMessageTemplate()
	 */
	@Override
	public String getDefaultConstraintMessageTemplate() {
		// TODO Auto-generated method stub
		return null;
	}

}
