package org.duracloud.chunk;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.log4j.Logger;
import org.duracloud.chunk.error.NotFoundException;
import org.duracloud.chunk.stream.ChunkInputStream;
import org.duracloud.chunk.writer.AddContentResult;
import org.duracloud.chunk.writer.ContentWriter;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.common.util.ExceptionUtil;

import java.io.*;
import java.security.DigestInputStream;
import java.util.Collection;
import java.util.List;
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

    private final Logger log = Logger.getLogger(getClass());

    private ContentWriter contentWriter;
    private FileChunkerOptions options;

    public FileChunker(ContentWriter contentWriter) {
        this(contentWriter, new FileChunkerOptions());
    }

    public FileChunker(ContentWriter contentWriter,
                       FileChunkerOptions options) {
        this.contentWriter = contentWriter;
        this.options = options;
    }

    protected void writeReport(File outputFile) {
        StringBuilder sb = new StringBuilder();
        if (!outputFile.exists()) {
            sb.append("spaceId,contentId,md5,size,state\n");
        }

        OutputStream outputStream = getOutputStream(outputFile);

        List<AddContentResult> results = contentWriter.getResults();
        for (AddContentResult result : results) {
            sb.append(result.getSpaceId());
            sb.append(",");
            sb.append(result.getContentId());
            sb.append(",");
            sb.append(result.getMd5());
            sb.append(",");
            sb.append(result.getContentSize());
            sb.append(",");
            sb.append(result.getState().name());
            sb.append("\n");
        }

        try {
            outputStream.write(sb.toString().getBytes());
        } catch (IOException e) {
            throw new DuraCloudRuntimeException(e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    private OutputStream getOutputStream(File outputFile) {
        boolean append = true;
        try {
            return new FileOutputStream(outputFile, append);
        } catch (IOException e) {
            throw new DuraCloudRuntimeException(e);
        }
    }

    /**
     * These methods loops the arg baseDir and pushes the found content to the
     * arg destSpace.
     *
     * @param baseDir     of content to push to DataStore
     * @param destSpaceId of content destination
     */
    protected void addContentFrom(File baseDir, String destSpaceId) {

        Collection<File> files = listFiles(baseDir,
                                           options.getFileFilter(),
                                           options.getDirFilter());
        for (File file : files) {
            try {
                doAddContent(baseDir, destSpaceId, file);

            } catch (Exception e) {
                StringBuilder sb = new StringBuilder("Error: ");
                sb.append("Unable to addContentFrom [");
                sb.append(baseDir);
                sb.append(", ");
                sb.append(destSpaceId);
                sb.append("] : ");
                sb.append(e.getMessage());
                sb.append("\n");
                sb.append(ExceptionUtil.getStackTraceAsString(e));
                System.err.println(sb);
                log.error(sb);
            }
        }
    }

    private void doAddContent(File baseDir, String destSpaceId, File file)
        throws NotFoundException {
        long maxChunkSize = options.getMaxChunkSize();
        boolean ignoreLargeFiles = options.isIgnoreLargeFiles();
        boolean preserveChunkMD5s = options.isPreserveChunkMD5s();

        String contentId = getContentId(baseDir, file);
        InputStream stream = getInputStream(file);
        long fileSize = file.length();

        log.debug("loading file: " + contentId + "[" + fileSize + "]");
        if (fileSize <= maxChunkSize) {
            BufferedInputStream buffStream = new BufferedInputStream(stream);
            ChunkInputStream chunk = new ChunkInputStream(contentId,
                                                          buffStream,
                                                          fileSize,
                                                          preserveChunkMD5s);

            contentWriter.writeSingle(destSpaceId, chunk);

        } else if (!ignoreLargeFiles) {
            ChunkableContent chunkable = new ChunkableContent(contentId,
                                                              stream,
                                                              fileSize,
                                                              maxChunkSize);
            chunkable.setPreserveChunkMD5s(preserveChunkMD5s);

            contentWriter.write(destSpaceId, chunkable);

        } else {
            log.info("Ignoring: [" + baseDir.getPath() + "," + contentId + "]");
            contentWriter.ignore(destSpaceId, contentId, fileSize);
        }

        IOUtils.closeQuietly(stream);
    }

    private Collection<File> listFiles(File baseDir,
                                       IOFileFilter fileFilter,
                                       IOFileFilter dirFilter) {
        if (!baseDir.isDirectory()) {
            throw new DuraCloudRuntimeException("Invalid dir: " + baseDir);
        }

        Collection files = FileUtils.listFiles(baseDir, fileFilter, dirFilter);
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
        FileOutputStream fos = new FileOutputStream(outFile);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter bw = new BufferedWriter(osw, BUF_SZ);

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