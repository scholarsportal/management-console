
package org.duraspace.storage.provider;

import java.io.InputStream;

import java.util.Iterator;
import java.util.Map;

import org.duraspace.storage.domain.StorageException;

public class BrokeredStorageProvider
        implements StorageProvider {

    private final StatelessStorageProvider dispatchProvider;

    private final StorageProvider targetProvider;

    public BrokeredStorageProvider(StatelessStorageProvider dispatchProvider,
                                   StorageProvider targetProvider) {
        this.dispatchProvider = dispatchProvider;
        this.targetProvider = targetProvider;
    }

    public String addContent(String spaceId,
                           String contentId,
                           String contentMimeType,
                           long contentSize,
                           InputStream content) throws StorageException {

        return dispatchProvider.addContent(targetProvider,
                                           spaceId,
                                           contentId,
                                           contentMimeType,
                                           contentSize,
                                           content);
    }

    public void createSpace(String spaceId) throws StorageException {
        dispatchProvider.createSpace(targetProvider, spaceId);

    }

    public void deleteContent(String spaceId, String contentId)
            throws StorageException {
        dispatchProvider.deleteContent(targetProvider, spaceId, contentId);

    }

    public void deleteSpace(String spaceId) throws StorageException {
        dispatchProvider.deleteSpace(targetProvider, spaceId);

    }

    public InputStream getContent(String spaceId, String contentId)
            throws StorageException {
        return dispatchProvider.getContent(targetProvider, spaceId, contentId);
    }

    public Map<String, String> getContentMetadata(String spaceId, String contentId)
            throws StorageException {
        return dispatchProvider.getContentMetadata(targetProvider,
                                                   spaceId,
                                                   contentId);
    }

    public AccessType getSpaceAccess(String spaceId) throws StorageException {
        return dispatchProvider.getSpaceAccess(targetProvider, spaceId);
    }

    public Iterator<String> getSpaceContents(String spaceId)
            throws StorageException {
        return dispatchProvider.getSpaceContents(targetProvider, spaceId);
    }

    public Map<String, String> getSpaceMetadata(String spaceId) throws StorageException {
        return dispatchProvider.getSpaceMetadata(targetProvider, spaceId);

    }

    public Iterator<String> getSpaces() throws StorageException {
        return dispatchProvider.getSpaces(targetProvider);
    }

    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Map<String, String> contentMetadata)
            throws StorageException {
        dispatchProvider.setContentMetadata(targetProvider,
                                            spaceId,
                                            contentId,
                                            contentMetadata);
    }

    public void setSpaceAccess(String spaceId, AccessType access)
            throws StorageException {
        dispatchProvider.setSpaceAccess(targetProvider, spaceId, access);
    }

    public void setSpaceMetadata(String spaceId, Map<String, String> spaceMetadata)
            throws StorageException {
        dispatchProvider.setSpaceMetadata(targetProvider,
                                          spaceId,
                                          spaceMetadata);
    }

}
