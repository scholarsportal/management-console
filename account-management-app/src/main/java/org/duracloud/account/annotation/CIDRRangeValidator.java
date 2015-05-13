/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */

public class CIDRRangeValidator
    implements ConstraintValidator<CIDRRangeConstraint, String> {
    private CIDRRangeConstraint constraintAnnotation;
    
    /*
     * (non-Javadoc)
     * 
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
     * javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //may contain multiple values, semicolon separated.
        if(!StringUtils.isBlank(value)){
            String[] values = value.split(";");
            for(String val : values){
                try{
                    new IpAddressMatcher(val);
                }catch(IllegalArgumentException ex){
                    return false;
                }
            }
            return true;
        }else{
            return true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.validation.ConstraintValidator#initialize(java.lang.annotation.
     * Annotation)
     */
    @Override
    public void initialize(CIDRRangeConstraint constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

}
