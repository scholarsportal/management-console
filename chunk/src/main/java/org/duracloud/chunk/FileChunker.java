package org.duracloud.chunk;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.chunk.writer.ContentWriter;
import org.duracloud.chunk.error.NotFoundException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.util.Collection;
import java.util.Random;

/**
 * This class provides the ability to loop over a directory of content which
 * may include files over 5-GB in size, chunk, and push them to a DataStore
 * encapsulated by the member: ContentWriter.
 * A ContentWriter may push to any DataStore, such as:
 * - a filesystem or
 * - a DuraCloud space
 *
 * @author Andrew Woods
 *         Date: Feb 4, 2010
 */
public class FileChunker {

    private ContentWriter contentWriter;
    private final long maxChunkSize;

    public FileChunker(ContentWriter contentWriter, long maxChunkSize) {
        this.contentWriter = contentWriter;
        this.maxChunkSize = maxChunkSize;
    }

    /**
     * These methods loop the arg baseDir and push the found content to the
     * arg destSpace.
     *
     * @param baseDir     of content to push to DataStore
     * @param destSpaceId of content destination
     */
    protected void addContentFrom(File baseDir,
                                  String destSpaceId,
                                  boolean preserveChunkMD5s)
        throws NotFoundException {
        this.addContentFrom(baseDir,
                            destSpaceId,
                            TrueFileFilter.TRUE,
                            preserveChunkMD5s);
    }

    /**
     * This method is the same as above with the additional ability to only
     * include the files defined in the arg "includes" filter in the push.
     *
     * @param baseDir     of content to push to DataStore
     * @param destSpaceId of content destination
     * @param includes    file filter defining subset of files to push
     */
    protected void addContentFrom(File baseDir,
                                  String destSpaceId,
                                  IOFileFilter includes,
                                  boolean preserveChunkMD5s)
        throws NotFoundException {
        String contentId;
        InputStream stream;
        ChunkableContent chunkable;

        Collection<File> files = listFiles(baseDir, includes);
        for (File file : files) {
            contentId = getContentId(baseDir, file);
            stream = getInputStream(file);
            chunkable = new ChunkableContent(contentId,
                                             stream,
                                             file.length(),
                                             maxChunkSize);
            chunkable.setPreserveChunkMD5s(preserveChunkMD5s);

            contentWriter.write(destSpaceId, chunkable);
        }
    }

    private Collection<File> listFiles(File baseDir, IOFileFilter includes) {
        if (!baseDir.isDirectory()) {
            throw new DuraCloudRuntimeException("Invalid dir: " + baseDir);
        }

        IOFileFilter all = TrueFileFilter.TRUE;
        Collection files = FileUtils.listFiles(baseDir, includes, all);
        if (null == files || files.size() == 0) {
            throw new DuraCloudRuntimeException("No files found: " + baseDir);
        }

        return files;
    }

    /**
     * This method defines the returned contentId as the path of the arg file
     * minus the path of the arg baseDir, in which the file was found.
     *
     * @param baseDir dir that contained the arg file or one of its parents
     * @param file    for which contentId is to be found
     * @return contentId of arg file
     */
    private String getContentId(File baseDir, File file) {
        String filePath = file.getPath();
        String basePath = baseDir.getPath();

        int index = filePath.indexOf(basePath);
        if (index == -1) {
            StringBuilder sb = new StringBuilder("Invalid basePath for file: ");
            sb.append("b: '" + basePath + "', ");
            sb.append("f: '" + filePath + "'");
            throw new DuraCloudRuntimeException(sb.toString());
        }

        String contentId = filePath.substring(index + basePath.length());
        if (contentId.startsWith(File.separator)) {
            contentId = contentId.substring(1, contentId.length());
        }
        return contentId;
    }

    private InputStream getInputStream(File file) {
        try {
            return new AutoCloseInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new DuraCloudRuntimeException(e.getMessage(), e);
        }
    }

    /**
     * This method generates a test file with random char content.
     *
     * @param outFile of test file
     * @param size    number of bytes in test file
     * @return DigestInputStream of test file
     * @throws IOException on error
     */
    protected static DigestInputStream createTestContent(File outFile,
                                                         long size)
        throws IOException {
        final int BUF_SZ = 8192;
        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile), BUF_SZ);

        int MIN_CHAR = 32;
        int MAX_CHAR_MINUS_MIN_CHAR = 126 - MIN_CHAR;
        Random r = new Random();
        for (long i = 0; i < size; ++i) {

            if (i % 101 == 0) {
                bw.newLine();
            } else {
                bw.write(r.nextInt(MAX_CHAR_MINUS_MIN_CHAR) + MIN_CHAR);
            }
        }
        IOUtils.closeQuietly(bw);

        return ChecksumUtil.wrapStream(new FileInputStream(outFile),
                                       ChecksumUtil.Algorithm.MD5);
    }

}
