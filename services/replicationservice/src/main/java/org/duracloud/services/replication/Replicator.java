
package org.duracloud.services.replication;

import java.io.InputStream;

import java.util.Map;

import org.duraspace.client.ContentStore;
import org.duraspace.client.ContentStoreManager;
import org.duraspace.domain.Content;
import org.duraspace.domain.Space;
import org.duraspace.storage.domain.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs replication activities
 *
 * @author Bill Branan
 */
public class Replicator {

    private static final Logger log =
        LoggerFactory.getLogger(ReplicationService.class);

    private ContentStore fromStore;
    private ContentStore toStore;

    public Replicator(String host,
                      String port,
                      String context,
                      String fromStoreId,
                      String toStoreId) {
        ContentStoreManager storeManager =
            new ContentStoreManager(host, port, context);

        try {
            fromStore = storeManager.getContentStore(fromStoreId);
            toStore = storeManager.getContentStore(toStoreId);
        } catch(StorageException se) {
            String error = "Unable to create connections to content " +
            		       "stores for replication " + se.getMessage();
            log.error(error);
            System.out.println(error); //TODO: Remove once logging works
        }
    }

    public void replicateSpace(String spaceId) {
        if(log.isDebugEnabled()) {
            log.debug("Performing Replication for " + spaceId +
                      " from " + fromStore.getStorageProviderType().name() +
                      " to " + toStore.getStorageProviderType().name());
        }

        //TODO: Remove once logging works --
        System.out.println("Performing Replication for " + spaceId +
                           " from " + fromStore.getStorageProviderType().name() +
                           " to " + toStore.getStorageProviderType().name());
        //TODO: -- Remove once logging works

        try {
            Space space = fromStore.getSpace(spaceId);
            toStore.createSpace(spaceId, space.getMetadata());
        } catch (StorageException se) {
            String error = "Unable to replicate space " + spaceId +
                           " due to error: " + se.getMessage();
            log.error(error, se);
            System.out.println(error); //TODO: Remove once logging works
        }
    }

    public void replicateContent(String spaceId, String contentId) {
        if(log.isDebugEnabled()) {
            log.debug("Performing Replication for " + spaceId + "/" + contentId +
                      " from " + fromStore.getStorageProviderType().name() +
                      " to " + toStore.getStorageProviderType().name());
        }

        //TODO: Remove once logging works --
        System.out.println("Performing Replication for " + spaceId + "/" + contentId +
                           " from " + fromStore.getStorageProviderType().name() +
                           " to " + toStore.getStorageProviderType().name());
        //TODO: -- Remove once logging works

        try {
            Space toSpace = toStore.getSpace(spaceId);
            System.out.println("toSpace: " + toSpace); //TODO: Remove
        } catch(StorageException se) {
            System.out.println("Space " + spaceId + " does not exist at " +
                               toStore.getStorageProviderType().name()); //TODO: Remove
            replicateSpace(spaceId);
        }

        try {
            Content content = fromStore.getContent(spaceId, contentId);
            InputStream contentStream = content.getStream();
            if(contentStream != null) {
                Map<String, String> metadata = content.getMetadata();

                String mimeType = "application/octet-stream";
                long contentSize = 0;

                if(metadata != null) {
                    mimeType = metadata.get(ContentStore.CONTENT_MIMETYPE);

                    String size = metadata.get(ContentStore.CONTENT_SIZE);
                    if(size != null) {
                        try {
                            contentSize = Long.valueOf(size);
                        } catch(NumberFormatException nfe) {
                            log.warn("Could not convert stream size header " +
                            		 "value '" + size + "' to a number");
                            contentSize = 0;
                        }
                    }
                }

                toStore.addContent(spaceId,
                                   contentId,
                                   contentStream,
                                   contentSize,
                                   mimeType,
                                   metadata);
            } else {
                throw new StorageException("The content stream retrieved " +
                                           "from the store was null.");
            }
        } catch(StorageException se) {
            String error = "Unable to replicate content " + contentId + " in space " +
                           spaceId + " due to error: " + se.getMessage();
            log.error(error, se);
            System.out.println(error); //TODO: Remove once logging works
        }

        if(log.isDebugEnabled()) {
            log.debug("Replication Completed");
        }
        System.out.println("Replication Completed"); //TODO: Remove once logging works
    }

}
