package org.duracloud.client.error;

import org.duracloud.common.util.error.DuraCloudException;

/**
 * Exception thrown when a response from a services call returns a
 * 404 NOT FOUND response. The message of the exception should indicate
 * which part of the call was not found.
 *
 * @author Bill Branan
 */
public class NotFoundException extends DuraCloudException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }

}