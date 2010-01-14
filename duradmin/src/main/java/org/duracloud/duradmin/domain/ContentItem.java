
package org.duracloud.duradmin.domain;

import org.duracloud.common.web.EncodeUtil;
import org.duracloud.duradmin.util.FileData;
import org.duracloud.duradmin.util.MetadataUtils;
import org.duracloud.duradmin.util.NameValuePair;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ContentItem
        implements Serializable {

    private static final long serialVersionUID = -5835779644282347055L;

    private String action;

    private String spaceId;

    private String contentId;

    private String contentMimetype;

    private List<NameValuePair> extendedMetadata;

    private transient MultipartFile file;

    private FileData fileData = new FileData();

    public long getSize() {
        return fileData.getSize();
    }

    private ContentMetadata metadata;

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
        if (StringUtils.hasText(contentId)) {
            return contentId;
        }
        return fileData.getName();
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getEncodedContentId() {
        String contentId = getContentId();
        return EncodeUtil.urlEncode(contentId);
    }

    public String getContentMimetype() {
        if (StringUtils.hasText(this.contentMimetype)) {
            return this.contentMimetype;
        }
        return this.fileData.getMimetype();
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

    public void setFile(MultipartFile file) throws IOException {
        if (file == null) {
            return;
        }

        this.file = file;

        this.fileData.setFile(file);
    }

    public MultipartFile getFile() {
        return file;
    }

    public FileData getFileData() {
        return this.fileData;
    }

    public List<NameValuePair> getExtendedMetadata() {
        return extendedMetadata;
    }

    public void setExtendedMetadata(Map<String, String> extendedMetadata) {
        this.extendedMetadata =
                MetadataUtils.convertExtendedMetadata(extendedMetadata);
    }

}
