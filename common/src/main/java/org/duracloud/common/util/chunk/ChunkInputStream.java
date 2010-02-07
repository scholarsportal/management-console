package org.duracloud.common.util.chunk;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.log4j.Logger;
import org.duracloud.common.error.DuraCloudRuntimeException;

import java.io.BufferedInputStream;
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
    private long maxChunkSize;
    private String mimetype;

    private boolean endFound = false; // marks end of wrapped stream.
    private String contentId;

    public ChunkInputStream(String chunkId,
                            InputStream inputStream,
                            long maxChunkSize) {
        this(chunkId, inputStream, maxChunkSize, "application/octet-stream");
    }

    public ChunkInputStream(String chunkId,
                            InputStream inputStream,
                            long maxChunkSize,
                            String mimetype) {
        final int BUFFER_SIZE = calculateBufferSize(maxChunkSize);
        this.stream = new CountingInputStream(new BufferedInputStream(
            inputStream,
            BUFFER_SIZE));

        this.chunkId = chunkId;
        this.maxChunkSize = maxChunkSize;
        this.mimetype = mimetype;
    }

    /**
     * This method finds the maximum 1-KB divisor of arg maxChunkSize that is
     * less than 8-KB.
     * It also ensures that arg maxChunkSize is a multiple of 1-KB, otherwise
     * the stream buffering would lose bytes if the maxChunkSize was not
     * divisible by the buffer size.
     * Additionally, by making the buffer multiples of 1-KB ensures efficient
     * block-writing.
     *
     * @param maxChunkSize of chunk stream
     * @return efficient buffer size for given arg chunk-size
     */
    private int calculateBufferSize(long maxChunkSize) {
        final int KB = 1024;

        // Ensure maxChunkSize falls on 1-KB boundaries.
        if (maxChunkSize % KB != 0) {
            throw new DuraCloudRuntimeException(
                "MaxChunkSize must be multiple of 1024: " + maxChunkSize);
        }

        // Find maximum block factor less than 8-KB.
        long size = maxChunkSize;
        while (size > 8 * KB) {
            size /= 2;
        }

        log.debug("Buf size: " + size + " for maxChunkSize: " + maxChunkSize);
        return (int) size;
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

    public String getChunkId() {
        return chunkId;
    }
}
