
package org.duracloud.services.replication;

import java.io.InputStream;

import java.util.Map;

import org.duraspace.client.ContentStore;
import org.duraspace.client.ContentStoreManager;
import org.duraspace.domain.Content;
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
                      String fromStoreID,
                      String toStoreID) {
        ContentStoreManager storeManager =
            new ContentStoreManager(host, port, context);

        try {
            fromStore = storeManager.getContentStore(fromStoreID);
            toStore = storeManager.getContentStore(toStoreID);
        } catch(StorageException se) {
            String error = "Unable to create connections to content " +
            		       "stores for replication " + se.getMessage();
            System.out.println(error); //TODO: Remove once logging works
            log.error(error);
        }
    }

    public void replicate(String spaceID, String contentID) {
        if(log.isDebugEnabled()) {
            log.debug("Performing Replication for " + spaceID + "/" + contentID);
        }

        //TODO: Remove --
        System.out.println("Performing Replication...");
        System.out.println("Replicating content item from " +
                           fromStore.getStorageProviderType().name());
        System.out.println("Replicating content item to " +
                           toStore.getStorageProviderType().name());
        //TODO: -- Remove

        try {
            Content content = fromStore.getContent(spaceID, contentID);
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

                toStore.addContent(spaceID,
                                   contentID,
                                   contentStream,
                                   contentSize,
                                   mimeType,
                                   metadata);
            } else {
                throw new StorageException("The content stream retrieved " +
                                           "from the store was null.");
            }
        } catch(StorageException se) {
            String error = "Unable to replicate content " + contentID + " in space " +
                           spaceID + " due to error: " + se.getMessage();
            log.error(error, se);
            System.out.println(error); //TODO: Remove once logging works
        }

        System.out.println("Replication Complete"); //TODO: Remove
    }

}
