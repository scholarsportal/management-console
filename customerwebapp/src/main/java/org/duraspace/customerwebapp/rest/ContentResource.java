package org.duraspace.customerwebapp.rest;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import org.duraspace.common.web.RestResourceException;
import org.duraspace.customerwebapp.util.StorageProviderFactory;
import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.provider.StorageProvider;
import org.duraspace.storage.provider.StorageProvider.AccessType;

/**
 * Provides interaction with content
 *
 * @author Bill Branan
 */
public class ContentResource {

    private static final Logger log = Logger.getLogger(ContentResource.class);

    /**
     * Retrieves content from a space.
     *
     * @param spaceID
     * @param contentID
     * @return InputStream which can be used to read content.
     */
    public static InputStream getContent(String spaceID,
                                         String contentID,
                                         String storeID)
    throws RestResourceException {
        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);

            AccessType access = storage.getSpaceAccess(spaceID);
            if(access.equals(AccessType.CLOSED)) {
                // TODO: Check user permissions
            }

            return storage.getContent(spaceID, contentID);
        } catch (StorageException e) {
            String error = "Error attempting to get content '" + contentID +
                           "' in '" + spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

    /**
     * Retrieves the metadata of a piece of content.
     *
     * @param spaceID
     * @param contentID
     * @return Map of content metadata
     */
    public static Map<String, String> getContentMetadata(String spaceID,
                                                         String contentID,
                                                         String storeID)
    throws RestResourceException {
        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);

            AccessType access = storage.getSpaceAccess(spaceID);
            if(access.equals(AccessType.CLOSED)) {
                // TODO: Check user permissions
            }

            return storage.getContentMetadata(spaceID, contentID);
        } catch (StorageException e) {
            String error = "Error attempting to get metadata for content '" +
                           contentID + "' in '" + spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

    /**
     * Updates the metadata of a piece of content.
     *
     * @return success
     */
    public static void updateContentMetadata(String spaceID,
                                             String contentID,
                                             String contentName,
                                             String contentMimeType,
                                             Map<String, String> userMetadata,
                                             String storeID)
    throws RestResourceException {
        // TODO: Check user permissions
        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);

            Map<String, String> metadata =
                storage.getContentMetadata(spaceID, contentID);
            if(metadata == null) {
                metadata = new HashMap<String, String>();
            }
            boolean metadataUpdated = false;

            // Update content name if a new value was provided
            if(contentName != null && !contentName.equals("")) {
                metadata.put(StorageProvider.METADATA_CONTENT_NAME, contentName);
                metadataUpdated = true;
            }

            // Update content mime type if a new value was provided
            if(contentMimeType != null && !contentMimeType.equals("")) {
                metadata.put(StorageProvider.METADATA_CONTENT_MIMETYPE, contentMimeType);
                metadataUpdated = true;
            }

            // Update user metadata
            if(userMetadata != null && userMetadata.size() > 0) {
                Iterator<String> userMetaNames = userMetadata.keySet().iterator();
                while(userMetaNames.hasNext()) {
                    String userMetaName = userMetaNames.next();
                    String userMetaValue = userMetadata.get(userMetaName);
                    metadata.put(userMetaName, userMetaValue);
                }
                metadataUpdated = true;
            }

            if(metadataUpdated) {
                storage.setContentMetadata(spaceID, contentID, metadata);
            }
        } catch (StorageException e) {
            String error = "Error attempting to update metadata for content '" +
                           contentID + "' in '" + spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

    /**
     * Adds content to a space.
     *
     * @return success
     */
    public static void addContent(String spaceID,
                                  String contentID,
                                  InputStream content,
                                  String contentMimeType,
                                  int contentSize,
                                  String storeID)
    throws RestResourceException {
        // TODO: Check user permissions

        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);

            storage.addContent(spaceID,
                               contentID,
                               contentMimeType,
                               contentSize,
                               content);
        } catch (StorageException e) {
            String error = "Error attempting to add content '" + contentID +
                           "' in '" + spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

    /**
     * Removes a piece of content.
     *
     * @param spaceID
     * @param contentID
     * @return success
     */
    public static void deleteContent(String spaceID,
                                     String contentID,
                                     String storeID)
    throws RestResourceException {
        // TODO: Check user permissions

        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);

            storage.deleteContent(spaceID, contentID);
        } catch (StorageException e) {
            String error = "Error attempting to delete content '" + contentID +
                           "' in '" + spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

}
