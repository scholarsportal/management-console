
package org.duraspace.storage.provider;

import java.io.InputStream;

import java.util.List;
import java.util.Properties;

import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.provider.StorageProvider.AccessType;

public class StatelessStorageProviderImpl
        implements StatelessStorageProvider {

    /**
     * {@inheritDoc}
     */
    public void addContent(StorageProvider targetProvider,
                           String spaceId,
                           String contentId,
                           String contentMimeType,
                           long contentSize,
                           InputStream content) throws StorageException {
        targetProvider.addContent(spaceId,
                                  contentId,
                                  contentMimeType,
                                  contentSize,
                                  content);
    }

    /**
     * {@inheritDoc}
     */
    public void createSpace(StorageProvider targetProvider, String spaceId)
            throws StorageException {
        targetProvider.createSpace(spaceId);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteContent(StorageProvider targetProvider,
                              String spaceId,
                              String contentId) throws StorageException {
        targetProvider.deleteContent(spaceId, contentId);

    }

    /**
     * {@inheritDoc}
     */
    public void deleteSpace(StorageProvider targetProvider, String spaceId)
            throws StorageException {
        targetProvider.deleteSpace(spaceId);

    }

    /**
     * {@inheritDoc}
     */
    public InputStream getContent(StorageProvider targetProvider,
                                  String spaceId,
                                  String contentId) throws StorageException {
        return targetProvider.getContent(spaceId, contentId);
    }

    /**
     * {@inheritDoc}
     */
    public Properties getContentMetadata(StorageProvider targetProvider,
                                         String spaceId,
                                         String contentId)
            throws StorageException {
        return targetProvider.getContentMetadata(spaceId, contentId);
    }

    /**
     * {@inheritDoc}
     */
    public AccessType getSpaceAccess(StorageProvider targetProvider,
                                     String spaceId) throws StorageException {
        return targetProvider.getSpaceAccess(spaceId);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getSpaceContents(StorageProvider targetProvider,
                                         String spaceId)
            throws StorageException {
        return targetProvider.getSpaceContents(spaceId);
    }

    /**
     * {@inheritDoc}
     */
    public Properties getSpaceMetadata(StorageProvider targetProvider,
                                       String spaceId) throws StorageException {
        return targetProvider.getSpaceMetadata(spaceId);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getSpaces(StorageProvider targetProvider)
            throws StorageException {
        return targetProvider.getSpaces();
    }

    /**
     * {@inheritDoc}
     */
    public void setContentMetadata(StorageProvider targetProvider,
                                   String spaceId,
                                   String contentId,
                                   Properties contentMetadata)
            throws StorageException {
        targetProvider.setContentMetadata(spaceId, contentId, contentMetadata);

    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceAccess(StorageProvider targetProvider,
                               String spaceId,
                               AccessType access) throws StorageException {
        targetProvider.setSpaceAccess(spaceId, access);

    }

    /**
     * {@inheritDoc}
     */
    public void setSpaceMetadata(StorageProvider targetProvider,
                                 String spaceId,
                                 Properties spaceMetadata)
            throws StorageException {
        targetProvider.setSpaceMetadata(spaceId, spaceMetadata);

    }

}
