package org.duracloud.account.util.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * 
 * @author dbernstein
 *
 */
public class InvalidUsernameException extends DuraCloudCheckedException {

    private static final long serialVersionUID = 1L;

    public InvalidUsernameException(String username){
        super("Invalid username: " + username);
    }
}
