
package org.duraspace.domain;

import java.io.Serializable;

public class ContentItem implements Serializable {

    private static final long serialVersionUID = -5835779644282347055L;

    private String accountId;
    private String spaceId;
    private String contentId;
    private ContentMetadata metadata;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public ContentMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ContentMetadata metadata) {
        this.metadata = metadata;
    }

}
