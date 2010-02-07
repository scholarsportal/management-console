package org.duracloud.common.util.chunk;

/**
 * @author Andrew Woods
 *         Date: Feb 5, 2010
 */
public interface ContentWriter {

    /**
     * This method writes the ChunkableContent to the arg space.
     *
     * @param spaceId     destination where arg chunkable content will be written
     * @param contentSize of arg content
     * @param chunkable   content to be written
     */
    public void write(String spaceId,
                      long contentSize,
                      ChunkableContent chunkable);

}
