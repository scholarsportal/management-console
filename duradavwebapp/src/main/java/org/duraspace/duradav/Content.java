package org.duraspace.duradav;

import java.util.Date;

public class Content {

    private final Body body;

    private final long length;

    private final String mediaType;

    private final Date modifiedDate;

    public Content(Body body,
                   long length,
                   String mediaType,
                   Date modifiedDate) {
        this.body = body;
        this.length = length;
        this.mediaType = mediaType;
        this.modifiedDate = modifiedDate;
    }

    public Body getBody() {
        return body;
    }

    public long getLength() {
        return length;
    }

    public String getMediaType() {
        return mediaType;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

}
