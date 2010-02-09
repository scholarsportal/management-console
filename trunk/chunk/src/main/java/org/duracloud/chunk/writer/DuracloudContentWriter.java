package org.duracloud.chunk.writer;

import org.apache.log4j.Logger;
import org.duracloud.chunk.ChunkInputStream;
import org.duracloud.chunk.ChunkableContent;
import org.duracloud.chunk.ChunksManifest;
import org.duracloud.chunk.error.NotFoundException;
import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;

import java.util.Map;

/**
 * This class implements the ContentWriter interface to write the provided
 * content to the Duracloud storeclient interface.
 *
 * @author Andrew Woods
 *         Date: Feb 5, 2010
 */
public class DuracloudContentWriter implements ContentWriter {

    private final Logger log = Logger.getLogger(getClass());

    private ContentStore contentStore;

    public DuracloudContentWriter(ContentStore contentStore) {
        this.contentStore = contentStore;
    }

    /**
     * This method implements the ContentWriter interface for writing content
     * to a DataStore. In this case, the DataStore is durastore.
     *
     * @param spaceId   destination space of arg chunkable content
     * @param chunkable content to be written
     */
    public ChunksManifest write(String spaceId, ChunkableContent chunkable)
        throws NotFoundException {

        createSpaceIfNotExist(spaceId);

        Map<String, String> metadata = null;
        for (ChunkInputStream chunk : chunkable) {
            try {
                contentStore.addContent(spaceId,
                                        chunk.getChunkId(),
                                        chunk,
                                        chunk.getChunkSize(),
                                        chunk.getMimetype(),
                                        metadata);
            } catch (ContentStoreException e) {
                log.error(e.getFormattedMessage(), e);
                break;
            } catch (Exception ex) {
                log.error("Error adding content:" + ex.getMessage(), ex);
                break;
            }
        }
        return chunkable.finalizeManifest();
    }

    private void createSpaceIfNotExist(String spaceId)
        throws NotFoundException {
        // If space already exists, will not be recreated
        Map<String, String> metadata = null;
        try {
            contentStore.createSpace(spaceId, metadata);
        } catch (ContentStoreException e) {
            // do nothing.
        }

        int tries = 0;
        boolean exists;
        while (!(exists = spaceExists(spaceId)) && tries++ < 10) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // do nothing.
            }
        }

        if (!exists) {
            throw new NotFoundException("Space not found: " + spaceId);
        }
    }

    private boolean spaceExists(String spaceId) {
        try {
            return null != contentStore.getSpaceAccess(spaceId);
        } catch (ContentStoreException e) {
            return false;
        }
    }

}