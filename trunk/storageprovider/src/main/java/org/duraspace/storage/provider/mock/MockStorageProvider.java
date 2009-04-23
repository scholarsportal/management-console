
package org.duraspace.storage.provider.mock;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.provider.StorageProvider;

public class MockStorageProvider
        implements StorageProvider {

    private String spaceId;

    private String contentId;

    private String contentMimeType;

    private long contentSize;

    private InputStream content;

    private Properties contentMetadata;

    private AccessType access;

    private List<String> spaceContents;

    private Properties spaceMetadata;

    private List<String> spaces;

    public void addContent(String spaceId,
                           String contentId,
                           String contentMimeType,
                           long contentSize,
                           InputStream content) throws StorageException {
        this.spaceId = spaceId;
        this.contentId = contentId;
        this.contentMimeType = contentMimeType;
        this.contentSize = contentSize;
        this.content = content;
        spaceContents = new ArrayList<String>();
        spaceContents.add(content.toString());
    }

    public void createSpace(String spaceId) throws StorageException {
        this.spaceId = spaceId;
        spaces = new ArrayList<String>();
        spaces.add(spaceId);
    }

    public void deleteContent(String spaceId, String contentId)
            throws StorageException {
        this.spaceId = spaceId;
        this.contentId = contentId;
    }

    public void deleteSpace(String spaceId) throws StorageException {
        this.spaceId = spaceId;
    }

    public InputStream getContent(String spaceId, String contentId)
            throws StorageException {
        return content;
    }

    public Properties getContentMetadata(String spaceId, String contentId)
            throws StorageException {
        return contentMetadata;
    }

    public AccessType getSpaceAccess(String spaceId) throws StorageException {
        return access;
    }

    public List<String> getSpaceContents(String spaceId)
            throws StorageException {
        return spaceContents;
    }

    public Properties getSpaceMetadata(String spaceId) throws StorageException {
        return spaceMetadata;
    }

    public List<String> getSpaces() throws StorageException {
        return spaces;
    }

    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Properties contentMetadata)
            throws StorageException {
        this.spaceId = spaceId;
        this.contentId = contentId;
        this.contentMetadata = contentMetadata;
    }

    public void setSpaceAccess(String spaceId, AccessType access)
            throws StorageException {
        this.spaceId = spaceId;
        this.access = access;
    }

    public void setSpaceMetadata(String spaceId, Properties spaceMetadata)
            throws StorageException {
        this.spaceId = spaceId;
        this.spaceMetadata = spaceMetadata;
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

    public String getContentMimeType() {
        return contentMimeType;
    }

    public void setContentMimeType(String contentMimeType) {
        this.contentMimeType = contentMimeType;
    }

    public long getContentSize() {
        return contentSize;
    }

    public void setContentSize(long contentSize) {
        this.contentSize = contentSize;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public Properties getContentMetadata() {
        return contentMetadata;
    }

    public void setContentMetadata(Properties contentMetadata) {
        this.contentMetadata = contentMetadata;
    }

    public AccessType getAccess() {
        return access;
    }

    public void setAccess(AccessType access) {
        this.access = access;
    }

    public Properties getSpaceMetadata() {
        return spaceMetadata;
    }

    public void setSpaceMetadata(Properties spaceMetadata) {
        this.spaceMetadata = spaceMetadata;
    }

    public void setSpaces(List<String> spaces) {
        this.spaces = spaces;
    }

    public List<String> getSpaceContents() {
        return spaceContents;
    }


}
