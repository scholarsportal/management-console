package org.duracloud.chunk.writer;

import org.apache.log4j.Logger;
import org.duracloud.chunk.ChunkableContent;
import org.duracloud.chunk.error.ContentNotAddedException;
import org.duracloud.chunk.error.NotFoundException;
import org.duracloud.chunk.manifest.ChunksManifest;
import org.duracloud.chunk.stream.ChunkInputStream;
import org.duracloud.chunk.stream.KnownLengthInputStream;
import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private List<AddContentResult> results = new ArrayList<AddContentResult>();

    public DuracloudContentWriter(ContentStore contentStore) {
        this.contentStore = contentStore;
    }

    public List<AddContentResult> getResults() {
        return results;
    }

    public void ignore(String spaceId, String contentId, long contentSize) {
        AddContentResult result = new AddContentResult(spaceId,
                                                       contentId,
                                                       contentSize);
        result.setState(AddContentResult.State.IGNORED);
        results.add(result);
    }

    /**
     * This method implements the ContentWriter interface for writing content
     * to a DataStore. In this case, the DataStore is durastore.
     *
     * @param spaceId   destination space of arg chunkable content
     * @param chunkable content to be written
     * @throws NotFoundException if space is not found
     */
    public ChunksManifest write(String spaceId, ChunkableContent chunkable)
        throws NotFoundException {
        log.debug("write: " + spaceId);
        createSpaceIfNotExist(spaceId);

        for (ChunkInputStream chunk : chunkable) {
            writeSingle(spaceId, chunk);
        }

        ChunksManifest manifest = chunkable.finalizeManifest();
        addManifest(spaceId, manifest);

        log.debug("written: " + spaceId + ", " + manifest.getManifestId());
        return manifest;
    }

    /**
     * This method writes a single chunk to the DataStore.
     *
     * @param spaceId destination where arg chunk content will be written
     * @param chunk   content to be written
     * @return MD5 of written content
     * @throws NotFoundException if space is not found
     */
    public String writeSingle(String spaceId, ChunkInputStream chunk)
        throws NotFoundException {
        log.debug("writeSingle: " + spaceId + ", " + chunk.getChunkId());
        createSpaceIfNotExist(spaceId);

        addChunk(spaceId, chunk);

        log.debug("written: " + spaceId + ", " + chunk.getChunkId());
        return chunk.getMD5();
    }

    private void addChunk(String spaceId, ChunkInputStream chunk) {
        String chunkId = chunk.getChunkId();
        log.debug("addChunk: " + spaceId + ", " + chunkId);

        addContentThenReport(spaceId,
                             chunkId,
                             chunk,
                             chunk.getChunkSize(),
                             chunk.getMimetype());
    }

    private void addManifest(String spaceId, ChunksManifest manifest) {
        String manifestId = manifest.getManifestId();
        log.debug("addManifest: " + spaceId + ", " + manifestId);

        KnownLengthInputStream manifestBody = manifest.getBody();
        addContentThenReport(spaceId,
                             manifestId,
                             manifestBody,
                             manifestBody.getLength(),
                             manifest.getMimetype());
    }

    private void addContentThenReport(String spaceId,
                                      String contentId,
                                      InputStream contentStream,
                                      long contentSize,
                                      String contentMimetype) {
        AddContentResult result = new AddContentResult(spaceId,
                                                       contentId,
                                                       contentSize);
        String md5 = null;
        try {
            md5 = addContent(spaceId,
                             contentId,
                             contentStream,
                             contentSize,
                             contentMimetype);
        } catch (ContentNotAddedException e) {
            result.setState(AddContentResult.State.ERROR);
        }

        if (md5 != null) {
            result.setMd5(md5);
        }
        result.setState(AddContentResult.State.SUCCESS);

        results.add(result);
    }

    /**
     * @return MD5 of added content
     * @throws ContentNotAddedException
     */
    private String addContent(String spaceId,
                              String contentId,
                              InputStream contentStream,
                              long contentSize,
                              String contentMimetype)
        throws ContentNotAddedException {
        Map<String, String> metadata = null;
        try {
            return contentStore.addContent(spaceId,
                                           contentId,
                                           contentStream,
                                           contentSize,
                                           contentMimetype,
                                           metadata);
        } catch (ContentStoreException e) {
            log.error(e.getFormattedMessage(), e);
            throw new ContentNotAddedException(spaceId, contentId, e);
        } catch (Exception ex) {
            log.error("Error adding content:" + ex.getMessage(), ex);
            throw new ContentNotAddedException(spaceId, contentId, ex);
        }
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