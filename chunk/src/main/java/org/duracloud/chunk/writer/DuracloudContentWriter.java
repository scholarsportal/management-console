package org.duracloud.chunk.writer;

import org.apache.log4j.Logger;
import org.duracloud.chunk.ChunkableContent;
import org.duracloud.chunk.error.NotFoundException;
import org.duracloud.chunk.manifest.ChunksManifest;
import org.duracloud.chunk.stream.ChunkInputStream;
import org.duracloud.chunk.stream.KnownLengthInputStream;
import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private Set<String> existingSpaces = new HashSet<String>();

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
        log.debug("write: " + spaceId);
        createSpaceIfNotExist(spaceId);

        int tries;
        for (ChunkInputStream chunk : chunkable) {
            tries = 0;
            while (!addContent(spaceId, chunk) && tries++ < 5) {
                sleep(1000);
            }
        }

        ChunksManifest manifest = chunkable.finalizeManifest();
        tries = 0;
        while (!addContent(spaceId, manifest) && tries++ < 5) {
            sleep(1000);
        }

        log.debug("written: " + spaceId + ", " + manifest.getManifestId());
        return manifest;
    }

    public String writeSingle(String spaceId, ChunkInputStream chunk)
        throws NotFoundException {
        log.debug("writeSingle: " + spaceId + ", " + chunk.getChunkId());
        createSpaceIfNotExist(spaceId);

        int tries = 0;
        while (!addContent(spaceId, chunk) && tries++ < 5) {
            sleep(1000);
        }

        log.debug("written: " + spaceId + ", " + chunk.getChunkId());
        return chunk.getMD5();
    }

    private boolean addContent(String spaceId, ChunksManifest manifest) {
        log.debug("addContent: " + spaceId + ", " + manifest.getManifestId());
        KnownLengthInputStream manifestBody = manifest.getBody();
        Map<String, String> metadata = null;
        try {
            contentStore.addContent(spaceId,
                                    manifest.getManifestId(),
                                    manifestBody,
                                    manifestBody.getLength(),
                                    manifest.getMimetype(),
                                    metadata);
        } catch (ContentStoreException e) {
            log.error(e.getFormattedMessage(), e);
            return false;
        } catch (Exception ex) {
            log.error("Error adding content:" + ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    private boolean addContent(String spaceId, ChunkInputStream chunk) {
        log.debug("addContent: " + spaceId + ", " + chunk.getChunkId());
        Map<String, String> metadata = null;
        try {
            contentStore.addContent(spaceId,
                                    chunk.getChunkId(),
                                    chunk,
                                    chunk.getChunkSize(),
                                    chunk.getMimetype(),
                                    metadata);
        } catch (ContentStoreException e) {
            log.error(e.getFormattedMessage(), e);
            return false;
        } catch (Exception ex) {
            log.error("Error adding content:" + ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    private void createSpaceIfNotExist(String spaceId)
        throws NotFoundException {

        if (existingSpaces.contains(spaceId)) {
            return;
        }

        // If space already exists, will not be recreated
        createSpace(spaceId);

        int tries = 0;
        boolean exists;
        while (!(exists = spaceExists(spaceId)) && tries++ < 10) {
            sleep(1000);
        }

        if (!exists) {
            throw new NotFoundException("Space not found: " + spaceId);
        }

        existingSpaces.add(spaceId);
    }

    private void createSpace(String spaceId) {
        Map<String, String> metadata = null;
        try {
            contentStore.createSpace(spaceId, metadata);
        } catch (ContentStoreException e) {
            // do nothing.
        }
    }

    private boolean spaceExists(String spaceId) {
        try {
            return null != contentStore.getSpaceAccess(spaceId);
        } catch (ContentStoreException e) {
            return false;
        }
    }

    private void sleep(long napTime) {
        try {
            Thread.sleep(napTime);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

}