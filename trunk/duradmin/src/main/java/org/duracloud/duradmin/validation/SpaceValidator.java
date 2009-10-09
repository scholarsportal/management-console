package org.duracloud.duradmin.validation;

import org.duracloud.duradmin.domain.Space;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


public class SpaceValidator implements Validator {
    @SuppressWarnings("unchecked")
    @Override
    public boolean supports(Class clazz) {
        if(clazz.equals(Space.class)){
            return true;
        }
        return false;
    }
    
    
    @Override
    public void validate(Object target, Errors errors) {
        try{
            @SuppressWarnings("unused")
            Space space = (Space)target;
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "spaceId", "required", "Space Id must consist of a value with no spaces." );
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
    }

}
