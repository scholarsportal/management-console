package org.duracloud.chunk.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author Andrew Woods
 *         Date: Feb 8, 2010
 */
public class ContentNotAddedException extends DuraCloudCheckedException {

    private String spaceId = "";
    private String contentId = "";

    public ContentNotAddedException(String message) {
        super(message);
    }

    public ContentNotAddedException(String spaceId, Exception e) {
        super(spaceId, e);
    }

    public ContentNotAddedException(String spaceId,
                                    String contentId,
                                    Exception e) {
        super(spaceId, e);
        this.spaceId = spaceId;
        this.contentId = contentId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public String getContentId() {
        return contentId;
    }
}