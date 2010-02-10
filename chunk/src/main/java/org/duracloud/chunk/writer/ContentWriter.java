package org.duracloud.chunk.writer;

import org.duracloud.chunk.ChunkableContent;
import org.duracloud.chunk.manifest.ChunksManifest;
import org.duracloud.chunk.error.NotFoundException;

/**
 * @author Andrew Woods
 *         Date: Feb 5, 2010
 */
public interface ContentWriter {

    /**
     * This method writes the ChunkableContent to the arg space.
     *
     * @param spaceId   destination where arg chunkable content will be written
     * @param chunkable content to be written
     * @return ChunksManifest of written content
     */
    public ChunksManifest write(String spaceId, ChunkableContent chunkable)
        throws NotFoundException;

}
