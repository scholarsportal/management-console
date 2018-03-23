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

import org.duracloud.account.db.util.DuracloudUserService;
import org.duracloud.account.db.util.error.InvalidUsernameException;
import org.duracloud.account.db.util.error.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Daniel Bernstein
 * Date: Feb 8, 2012
 */

public class UsernameExistsValidator implements ConstraintValidator<UsernameExists, String> {

    @Autowired(required = true)
    private DuracloudUserService duracloudUserService;

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            this.duracloudUserService.checkUsername(value);
            return false;
        } catch (InvalidUsernameException ex) {
            return false;
        } catch (UserAlreadyExistsException ex) {
            return true;
        }
    }

    /* (non-Javadoc)
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(UsernameExists constraintAnnotation) {

    }

    public DuracloudUserService getUserService() {
        return duracloudUserService;
    }

    public void setUserService(DuracloudUserService duracloudUserService) {
        this.duracloudUserService = duracloudUserService;
    }

}
