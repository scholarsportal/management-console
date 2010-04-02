package org.duracloud.services.replication;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.common.model.Credential;
import org.duracloud.domain.Content;
import org.duracloud.error.ContentStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

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
                      Credential credential,
                      String fromStoreId,
                      String toStoreId) {
        ContentStoreManager storeManager =
            new ContentStoreManagerImpl(host, port, context);

        storeManager.login(credential);
        try {
            fromStore = storeManager.getContentStore(fromStoreId);
            toStore = storeManager.getContentStore(toStoreId);
        } catch(ContentStoreException cse) {
            String error = "Unable to create connections to content " +
            		       "stores for replication " + cse.getMessage();
            log.error(error);
            System.out.println(error); //TODO: Remove once logging works
        }
    }

    public void replicateSpace(String spaceId) {
        if(log.isDebugEnabled()) {
            log.debug("Performing Replication for " + spaceId +
                      " from " + fromStore.getStorageProviderType() +
                      " to " + toStore.getStorageProviderType());
        }

        //TODO: Remove once logging works --
        System.out.println("Performing Replication for " + spaceId +
                           " from " + fromStore.getStorageProviderType() +
                           " to " + toStore.getStorageProviderType());
        //TODO: -- Remove once logging works

        try {
            Map<String, String> spaceMeta = fromStore.getSpaceMetadata(spaceId);
            toStore.createSpace(spaceId, spaceMeta);
        } catch (ContentStoreException cse) {
            String error = "Unable to replicate space " + spaceId +
                           " due to error: " + cse.getMessage();
            log.error(error, cse);
            System.out.println(error); //TODO: Remove once logging works
        }
    }

    public void replicateContent(String spaceId, String contentId) {
        if(log.isDebugEnabled()) {
            log.debug("Performing Replication for " + spaceId + "/" + contentId +
                      " from " + fromStore.getStorageProviderType() +
                      " to " + toStore.getStorageProviderType());
        }

        //TODO: Remove once logging works --
        System.out.println("Performing Replication for " + spaceId + "/" + contentId +
                           " from " + fromStore.getStorageProviderType() +
                           " to " + toStore.getStorageProviderType());
        //TODO: -- Remove once logging works

        try {
            toStore.getSpaceMetadata(spaceId);
            System.out.println("toSpace: " + spaceId); //TODO: Remove
        } catch(ContentStoreException cse) {
            System.out.println("Space " + spaceId + " does not exist at " +
                               toStore.getStorageProviderType()); //TODO: Remove
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
                throw new ContentStoreException("The content stream retrieved " +
                                                "from the store was null.");
            }
        } catch(ContentStoreException cse) {
            String error = "Unable to replicate content " + contentId + " in space " +
                           spaceId + " due to error: " + cse.getMessage();
            log.error(error, cse);
            System.out.println(error); //TODO: Remove once logging works
        }

        if(log.isDebugEnabled()) {
            log.debug("Replication Completed");
        }
        System.out.println("Replication Completed"); //TODO: Remove once logging works
    }

}
