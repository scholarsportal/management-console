package org.duracloud.storage.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author: Bill Branan
 * Date: Jan 11, 2010
 */
public class InvalidIdException extends DuraCloudCheckedException {

    public InvalidIdException(String message) {
        super(message);
    }
}