/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.error;

/**
 * 
 * @author Daniel Bernstein
 *         Date: January 22, 2012
 *
 */
public class ReservedUsernameException extends InvalidUsernameException{

    private static final long serialVersionUID = 1L;
    
    public ReservedUsernameException(String username) {
        super(username + " is reserved.");
    }
}
