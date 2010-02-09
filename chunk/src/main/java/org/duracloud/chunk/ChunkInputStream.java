package org.duracloud.chunk;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class provides the ability to limit the number of bytes read from the
 * provided InputStream to maxChunkSize.
 *
 * @author Andrew Woods
 *         Date: Feb 2, 2010
 */
public class ChunkInputStream extends InputStream {
    private final Logger log = Logger.getLogger(getClass());

    private String chunkId;
    private CountingInputStream stream;
    private long chunkSize;
    private String mimetype;

    public ChunkInputStream(String chunkId,
                            InputStream inputStream,
                            long chunkSize) {
        this(chunkId, inputStream, chunkSize, "application/octet-stream");
    }

    public ChunkInputStream(String chunkId,
                            InputStream inputStream,
                            long chunkSize,
                            String mimetype) {
        this.stream = new CountingInputStream(inputStream);
        this.chunkId = chunkId;
        this.mimetype = mimetype;
        this.chunkSize = chunkSize;
    }

    /**
     * This method reads up to maxChunkSize number of bytes from the stream.
     * When either maxChunkSize bytes have been read, or the end of the stream
     * is reached, -1 is return.
     *
     * @return current byte or -1 if eof reached
     * @throws IOException on error
     */
    public int read() throws IOException {
        if (stream.getByteCount() >= chunkSize) {
            return -1;
        }

        return stream.read();
    }

    public void close() throws IOException {
        // do not allow the wrapped stream to be closed.
    }

    public long numBytesRead() {
        return stream.getByteCount();
    }

    public String getChunkId() {
        return chunkId;
    }

    public String getMimetype() {
        return mimetype;
    }

    public long getChunkSize() {
        return chunkSize;
    }
}
