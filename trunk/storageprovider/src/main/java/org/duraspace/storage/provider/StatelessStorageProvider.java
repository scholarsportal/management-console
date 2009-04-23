
package org.duraspace.storage.provider;

import java.io.InputStream;

import java.util.List;
import java.util.Properties;

import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.provider.StorageProvider.AccessType;

public interface StatelessStorageProvider {

    public abstract void addContent(StorageProvider targetProvider,
                                    String spaceId,
                                    String contentId,
                                    String contentMimeType,
                                    long contentSize,
                                    InputStream content)
            throws StorageException;

    public abstract void createSpace(StorageProvider targetProvider, String spaceId)
            throws StorageException;

    public abstract void deleteContent(StorageProvider targetProvider,
                                       String spaceId,
                                       String contentId)
            throws StorageException;

    public abstract void deleteSpace(StorageProvider targetProvider, String spaceId)
            throws StorageException;

    public abstract InputStream getContent(StorageProvider targetProvider,
                                           String spaceId,
                                           String contentId)
            throws StorageException;

    public abstract Properties getContentMetadata(StorageProvider targetProvider,
                                                  String spaceId,
                                                  String contentId)
            throws StorageException;

    public abstract AccessType getSpaceAccess(StorageProvider targetProvider,
                                              String spaceId)
            throws StorageException;

    public abstract List<String> getSpaceContents(StorageProvider targetProvider,
                                                  String spaceId)
            throws StorageException;

    public abstract Properties getSpaceMetadata(StorageProvider targetProvider,
                                                String spaceId)
            throws StorageException;

    public abstract List<String> getSpaces(StorageProvider targetProvider)
            throws StorageException;

    public abstract void setContentMetadata(StorageProvider targetProvider,
                                            String spaceId,
                                            String contentId,
                                            Properties contentMetadata)
            throws StorageException;

    public abstract void setSpaceAccess(StorageProvider targetProvider,
                                        String spaceId,
                                        AccessType access)
            throws StorageException;

    public abstract void setSpaceMetadata(StorageProvider targetProvider,
                                          String spaceId,
                                          Properties spaceMetadata)
            throws StorageException;

}