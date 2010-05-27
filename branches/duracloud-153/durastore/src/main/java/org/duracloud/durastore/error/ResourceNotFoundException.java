package org.duracloud.durastore.error;

/**
 * @author: Bill Branan
 * Date: Jan 8, 2010
 */
public class ResourceNotFoundException extends ResourceException {

    public ResourceNotFoundException(String task,
                                     String spaceId,
                                     Exception e) {
        super(task, spaceId, e);
    }

    public ResourceNotFoundException(String task,
                                     String spaceId,
                                     String contentId,
                                     Exception e) {
        super(task, spaceId, contentId, e);
    }
}
