package org.duracloud.error;

/**
 * Exception thrown when a requested space or content item does not exist.
 *
 * @author Bill Branan
 */
public class NotFoundException extends ContentStoreException {

    public NotFoundException (String message) {
        super(message);
    }

    public NotFoundException(String task, String spaceId, Exception e) {
        super(task, spaceId, e);
    }

    public NotFoundException(String task,
                             String spaceId,
                             String contentId,
                             Exception e) {
        super(task, spaceId, contentId, e);
    }

}