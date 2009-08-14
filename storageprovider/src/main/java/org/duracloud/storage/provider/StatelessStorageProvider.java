package org.duracloud.storage.provider;

import java.io.InputStream;

import java.util.Iterator;
import java.util.Map;

import org.duracloud.storage.domain.StorageException;
import org.duracloud.storage.provider.StorageProvider.AccessType;

public interface StatelessStorageProvider {

    public abstract String addContent(StorageProvider targetProvider,
                                      String storeId,
                                      String spaceId,
                                      String contentId,
                                      String contentMimeType,
                                      long contentSize,
                                      InputStream content)
            throws StorageException;

    public abstract void createSpace(StorageProvider targetProvider,
                                     String storeId,
                                     String spaceId)
            throws StorageException;

    public abstract void deleteContent(StorageProvider targetProvider,
                                       String storeId,
                                       String spaceId,
                                       String contentId)
            throws StorageException;

    public abstract void deleteSpace(StorageProvider targetProvider,
                                     String storeId,
                                     String spaceId)
            throws StorageException;

    public abstract InputStream getContent(StorageProvider targetProvider,
                                           String storeId,
                                           String spaceId,
                                           String contentId)
            throws StorageException;

    public abstract Map<String, String> getContentMetadata(StorageProvider targetProvider,
                                                           String storeId,
                                                           String spaceId,
                                                           String contentId)
            throws StorageException;

    public abstract AccessType getSpaceAccess(StorageProvider targetProvider,
                                              String storeId,
                                              String spaceId)
            throws StorageException;

    public abstract Iterator<String> getSpaceContents(StorageProvider targetProvider,
                                                      String storeId,
                                                      String spaceId)
            throws StorageException;

    public abstract Map<String, String> getSpaceMetadata(StorageProvider targetProvider,
                                                         String storeId,
                                                         String spaceId)
            throws StorageException;

    public abstract Iterator<String> getSpaces(StorageProvider targetProvider,
                                               String storeId)
            throws StorageException;

    public abstract void setContentMetadata(StorageProvider targetProvider,
                                            String storeId,
                                            String spaceId,
                                            String contentId,
                                            Map<String, String> contentMetadata)
            throws StorageException;

    public abstract void setSpaceAccess(StorageProvider targetProvider,
                                        String storeId,
                                        String spaceId,
                                        AccessType access)
            throws StorageException;

    public abstract void setSpaceMetadata(StorageProvider targetProvider,
                                          String storeId,
                                          String spaceId,
                                          Map<String, String> spaceMetadata)
            throws StorageException;

}