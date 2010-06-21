package org.duracloud.error;

/**
 * Exception thrown when a space or content ID is invalid.
 *
 * @author Bill Branan
 */
public class InvalidIdException extends ContentStoreException {

    public InvalidIdException(String message) {
        super(message);
    }

    public InvalidIdException(String task, String spaceId, Exception e) {
        super(task, spaceId, e);
    }

    public InvalidIdException(String task,
                             String spaceId,
                             String contentId,
                             Exception e) {
        super(task, spaceId, contentId, e);
    }

}