package org.duracloud.common.util.chunk;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Iterator;

/**
 * This class manages the provided content stream by breaking it chunks of the
 * size specified by maxChunkSize.
 *
 * @author Andrew Woods
 *         Date: Feb 2, 2010
 */
public class ChunkableContent implements Iterable<ChunkInputStream>, Iterator<ChunkInputStream> {

    private final Logger log = Logger.getLogger(getClass());

    private CountingInputStream largeStream;
    private String contentId;
    private long maxChunkSize;
    private ChunkInputStream currentChunk;

    public ChunkableContent(String contentId,
                            InputStream largeStream,
                            long maxChunkSize) {
        this.contentId = contentId;
        this.largeStream = new CountingInputStream(largeStream);
        this.maxChunkSize = maxChunkSize;
        this.currentChunk = nextChunk();
    }

    private int indexRemoveMe = 0;

    private ChunkInputStream nextChunk() {
        String chunkId = contentId + "-" + indexRemoveMe++;
        return currentChunk = new ChunkInputStream(chunkId,
                                                   largeStream,
                                                   maxChunkSize);
    }

    /**
     * This method indicates if there are any more chunk.
     *
     * @return true if more chunks are available.
     */
    public boolean hasNext() {
        return currentChunk != null && !currentChunk.endFound();
    }

    /**
     * This method the next chunk of the wrapped InputStream.
     *
     * @return next chunk as InputStream
     */
    public ChunkInputStream next() {
        return nextChunk();
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() not supported.");
    }

    public Iterator<ChunkInputStream> iterator() {
        return this;
    }

    public long getMaxChunkSize() {
        return maxChunkSize;
    }

    public void close() {
        log.debug("ChunkableContent.close()");
        IOUtils.closeQuietly(largeStream);
    }
}
