
package org.duraspace.storage.provider.mock;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.provider.StorageProvider;

public class MockStorageProvider
        implements StorageProvider {

    private String spaceId;

    private String contentId;

    private String contentMimeType;

    private long contentSize;

    private InputStream content;

    private Map<String, String> contentMetadata;

    private AccessType access;

    private Iterator<String> spaceContents;

    private Map<String, String> spaceMetadata;

    private Iterator<String> spaces;

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
        List<String> contentsList = new ArrayList<String>();
        contentsList.add(content.toString());
        spaceContents = contentsList.iterator();
    }

    public void createSpace(String spaceId) throws StorageException {
        this.spaceId = spaceId;
        List<String> spacesList = new ArrayList<String>();
        spacesList.add(spaceId);
        spaces = spacesList.iterator();
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

    public Map<String, String> getContentMetadata(String spaceId, String contentId)
            throws StorageException {
        return contentMetadata;
    }

    public AccessType getSpaceAccess(String spaceId) throws StorageException {
        return access;
    }

    public Iterator<String> getSpaceContents(String spaceId)
            throws StorageException {
        return spaceContents;
    }

    public Map<String, String> getSpaceMetadata(String spaceId)
    throws StorageException {
        return spaceMetadata;
    }

    public Iterator<String> getSpaces() throws StorageException {
        return spaces;
    }

    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Map<String, String> contentMetadata)
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

    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata)
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

    public Map<String, String> getContentMetadata() {
        return contentMetadata;
    }

    public void setContentMetadata(Map<String, String> contentMetadata) {
        this.contentMetadata = contentMetadata;
    }

    public AccessType getAccess() {
        return access;
    }

    public void setAccess(AccessType access) {
        this.access = access;
    }

    public Map<String, String> getSpaceMetadata() {
        return spaceMetadata;
    }

    public void setSpaceMetadata(Map<String, String> spaceMetadata) {
        this.spaceMetadata = spaceMetadata;
    }

    public void setSpaces(Iterator<String> spaces) {
        this.spaces = spaces;
    }

    public Iterator<String> getSpaceContents() {
        return spaceContents;
    }


}
