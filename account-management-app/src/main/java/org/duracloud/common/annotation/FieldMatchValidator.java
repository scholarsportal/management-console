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
public class FieldMatchValidator
    implements ConstraintValidator<FieldMatch, Object> {
    private FieldMatch constraintAnnotation;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(
        final Object value, final ConstraintValidatorContext context) {
        try {

            final Object firstObj =
                BeanUtils.getProperty(value, this.constraintAnnotation.first());
            final Object secondObj =
                BeanUtils.getProperty(value, this.constraintAnnotation.second());

            boolean doesNotMatch =
                firstObj == null
                    && secondObj == null || firstObj != null
                    && firstObj.equals(secondObj);

            if (!doesNotMatch) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(this.constraintAnnotation.message())
                .addNode(this.constraintAnnotation.second()).addConstraintViolation();
            }
            return doesNotMatch;
        } catch (final Exception ignore) {
            // ignore
        }
        return true;
    }
}
