package org.duracloud.chunk.writer;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.chunk.ChunkInputStream;
import org.duracloud.chunk.ChunkableContent;
import org.duracloud.chunk.ChunksManifest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class implements the ContentWriter interface to write the provided
 * content to a local filesystem.
 *
 * @author Andrew Woods
 *         Date: Feb 5, 2010
 */
public class FilesystemContentWriter implements ContentWriter {

    private final Logger log = Logger.getLogger(getClass());
    private static final long TWO_GB = 2000000000;

    /**
     * This method implements the ContentWriter interface for writing content
     * to a DataStore. In this case, the DataStore is a local filesystem.
     * The arg spaceId is the path to the destination directory.
     *
     * @param spaceId   destination where arg chunkable content will be written
     * @param chunkable content to be written
     */
    public ChunksManifest write(String spaceId, ChunkableContent chunkable) {
        File spaceDir = getSpaceDir(spaceId);

        File outFile;
        OutputStream outStream;
        for (ChunkInputStream chunk : chunkable) {
            outFile = getContentFile(spaceDir, chunk.getChunkId());
            outStream = getOutputStream(outFile);

            if (chunkable.getMaxChunkSize() > TWO_GB) {
                copyLarge(outStream, chunk);
            } else {
                copy(outStream, chunk);
            }

            flushAndClose(outStream);
        }
        return chunkable.finalizeManifest();
    }

    private void copyLarge(OutputStream outStream, ChunkInputStream chunk) {
        try {
            IOUtils.copyLarge(chunk, outStream);
        } catch (IOException e) {
            String msg = "Error in copy: " + chunk.getChunkId() + ": ";
            log.error(msg, e);
            throw new DuraCloudRuntimeException(msg + e.getMessage(), e);
        }
    }

    private void copy(OutputStream outStream, ChunkInputStream chunk) {
        try {
            IOUtils.copy(chunk, outStream);
        } catch (IOException e) {
            String msg = "Error in copy: " + chunk.getChunkId() + ": ";
            log.error(msg, e);
            throw new DuraCloudRuntimeException(msg + e.getMessage(), e);
        }
    }

    private OutputStream getOutputStream(File outFile) {
        final int BUFFER_SIZE = 8192;
        try {
            return new BufferedOutputStream(new FileOutputStream(outFile),
                                            BUFFER_SIZE);
        } catch (FileNotFoundException e) {
            throw new DuraCloudRuntimeException(e.getMessage(), e);
        }
    }

    private void flushAndClose(OutputStream outStream) {
        try {
            outStream.flush();
        } catch (IOException e) {
            // do nothing
        } finally {
            IOUtils.closeQuietly(outStream);
        }
    }

    private File getContentFile(File spaceDir, String contentId) {
        File contentFile = new File(spaceDir, contentId);
        File parent = contentFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        return contentFile;
    }

    private File getSpaceDir(String spaceId) {
        File spaceDir = new File(spaceId);
        if (!spaceDir.exists()) {
            spaceDir.mkdirs();
        }
        return spaceDir;
    }
}
