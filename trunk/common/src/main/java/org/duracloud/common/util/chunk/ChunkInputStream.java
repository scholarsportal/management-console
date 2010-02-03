package org.duracloud.common.util.chunk;

import org.apache.commons.io.input.CountingInputStream;

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
    private String chunkId;
    private CountingInputStream stream;
    private long maxChunkSize;
    private String mimetype;

    private boolean endFound = false; // marks end of wrapped stream.

    public ChunkInputStream(String chunkId,
                            InputStream inputStream,
                            long maxChunkSize) {
        this(chunkId, inputStream, maxChunkSize, "application/octet-stream");
    }

    public ChunkInputStream(String chunkId,
                            InputStream inputStream,
                            long maxChunkSize,
                            String mimetype) {
        this.chunkId = chunkId;
        this.stream = new CountingInputStream(inputStream);
        this.maxChunkSize = maxChunkSize;
        this.mimetype = mimetype;
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
        if (stream.getByteCount() >= maxChunkSize) {
            return -1;
        }

        int bite = stream.read();
        if (bite == -1) {
            endFound = true;
        }
        return bite;
    }

    public void close() throws IOException {
        // do not allow the wrapped stream to be closed.
    }

    /**
     * This method indicates if the end of the wrapped InputStream has been
     * reached.
     *
     * @return true if end of wrapped InputStream has been reached.
     */
    public boolean endFound() {
        return endFound;
    }
}
