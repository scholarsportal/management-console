package org.duracloud.error;

/**
 * Exception thrown when a request is not accepted because the caller is not
 * authorized to perform the function
 *
 * @author Bill Branan
 */
public class UnauthorizedException extends ContentStoreException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String task, String spaceId, Exception e) {
        super(task, spaceId, e);
    }

    public UnauthorizedException(String task,
                             String spaceId,
                             String contentId,
                             Exception e) {
        super(task, spaceId, contentId, e);
    }

}