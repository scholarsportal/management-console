package org.duracloud.durastore.rest;

import org.apache.log4j.Logger;
import org.duracloud.common.web.RestResourceException;
import org.duracloud.durastore.util.StorageProviderFactory;
import org.duracloud.storage.error.StorageException;
import org.duracloud.storage.provider.StorageProvider;
import org.duracloud.storage.provider.StorageProvider.AccessType;

import java.io.InputStream;
import java.util.Map;

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
                                             String contentMimeType,
                                             Map<String, String> userMetadata,
                                             String storeID)
    throws RestResourceException {
        // TODO: Check user permissions
        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);

            // Update content mime type if a new value was provided
            if(contentMimeType != null && !contentMimeType.equals("")) {
                userMetadata.put(StorageProvider.METADATA_CONTENT_MIMETYPE,
                                 contentMimeType);
            } else { // Keep mimetype as is
                Map<String, String> existingMeta =
                    storage.getContentMetadata(spaceID, contentID);
                String mimetype =
                    existingMeta.get(StorageProvider.METADATA_CONTENT_MIMETYPE);
                if(mimetype != null) {
                    userMetadata.put(StorageProvider.METADATA_CONTENT_MIMETYPE,
                                     mimetype);
                }
            }

            // Update content metadata
            if(userMetadata != null) {
                storage.setContentMetadata(spaceID, contentID, userMetadata);
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
     * @return the checksum of the content as computed by the storage provider
     */
    public static String addContent(String spaceID,
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

            return storage.addContent(spaceID,
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
