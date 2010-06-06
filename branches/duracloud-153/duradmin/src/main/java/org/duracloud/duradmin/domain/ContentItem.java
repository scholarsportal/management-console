
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

    public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}


	private String storeId;

    private String contentMimetype;

    private String viewerURL;
    
    private String downloadURL;
    
    private String thumbnailURL;

    private String tinyThumbnailURL;

    
    private List<NameValuePair> extendedMetadata;

    private transient MultipartFile file;

    private FileData fileData = new FileData();

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

    
    public String getDownloadURL() {
        return downloadURL;
    }

    
    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    
    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    
    public String getViewerURL() {
        return viewerURL;
    }

    
    public void setViewerURL(String viewerURL) {
        this.viewerURL = viewerURL;
    }

    
    public String getTinyThumbnailURL() {
        return tinyThumbnailURL;
    }

    
    public void setTinyThumbnailURL(String tinyThumbnailURL) {
        this.tinyThumbnailURL = tinyThumbnailURL;
    }

    public String toString(){
    	return "{storeId: " + storeId + ", spaceId: " + spaceId + ", contentId: " + contentId + 
    				", viewerURL: " + viewerURL + ", downloadURL: " + downloadURL + ", thumbnailURL: " + thumbnailURL + 
    				", metadata: " + metadata + ", contentMimetype: " + contentMimetype +"}";
    }
}
