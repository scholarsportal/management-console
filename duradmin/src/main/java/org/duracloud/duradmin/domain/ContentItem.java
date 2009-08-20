package org.duracloud.duradmin.domain;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class ContentItem implements Serializable {

    private static final long serialVersionUID = -5835779644282347055L;

    private String action;
    private String spaceId;
    private String contentId;
    private String contentMimetype;
    private ContentMetadata metadata;
    private MultipartFile file;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    public String getContentMimetype() {
        return contentMimetype;
    }

    public void setContentMimetype(String contentMimetype) {
        this.contentMimetype = contentMimetype;
    }

    public ContentMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ContentMetadata metadata) {
        this.metadata = metadata;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
