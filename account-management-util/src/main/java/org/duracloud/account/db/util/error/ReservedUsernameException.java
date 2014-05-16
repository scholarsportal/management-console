/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
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
