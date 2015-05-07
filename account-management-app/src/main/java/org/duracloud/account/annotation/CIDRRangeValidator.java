/*
 * Copyright (c) 2009-2015 DuraSpace. All rights reserved.
 */
package org.duracloud.account.annotation;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

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
        String ipv4Range = 
                "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
        		"([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(\\/([0-9]|[1-2][0-9]|3[0-2]))$";
        //may contain multiple values, semicolon separated.
        if(!StringUtils.isBlank(value)){
            String[] values = value.split(";");
            for(String val : values){
                if(!Pattern.matches(ipv4Range, val)){
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
