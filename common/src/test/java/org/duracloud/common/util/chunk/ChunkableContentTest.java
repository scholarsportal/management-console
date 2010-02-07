package org.duracloud.common.util.chunk;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.duracloud.common.util.ChecksumUtil;
import static org.duracloud.common.util.ChecksumUtil.Algorithm;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Andrew Woods
 *         Date: Feb 2, 2010
 */
public class ChunkableContentTest {

    private ChunkableContent chunkable;
    private String contentId;
    private final long MAX_CHUNK_SIZE = 10240;

    private DigestInputStream contentInputStream;
    private MessageDigest contentChecksum;
    private long contentSize;

    private List<File> chunkFiles;
    private File contentFile;

    private final String CHUNK_PREFIX = "a-chunk-";
    private final String LARGE_PREFIX = "a-large-";

    @Before
    public void setUp() throws IOException {
        contentSize = MAX_CHUNK_SIZE * 4 + (MAX_CHUNK_SIZE / 2);
        contentInputStream = createContent(contentSize);

        chunkFiles = new ArrayList<File>();
        chunkable = new ChunkableContent(contentId,
                                         contentInputStream,
                                         MAX_CHUNK_SIZE);
    }

    @After
    public void tearDown() {
        IOUtils.closeQuietly(contentInputStream);

        for (File f : chunkFiles) {
            FileUtils.deleteQuietly(f);
        }
        FileUtils.deleteQuietly(contentFile);
    }

    @Test
    public void testBasicChunking() throws Exception {
        doChunking();
        verifyTotalChunkLength();
        verifyTotalChunkChecksum();
    }

    private void doChunking() throws IOException {
        int i = 0;
        File f;
        FileOutputStream out;
        for (ChunkInputStream chunk : chunkable) {
            f = File.createTempFile(CHUNK_PREFIX + i++ + "-", ".txt");
            chunkFiles.add(f);

            out = new FileOutputStream(f);
            IOUtils.copy(chunk, out);

            IOUtils.closeQuietly(chunk);
            IOUtils.closeQuietly(out);
        }

        Assert.assertNotNull(chunkFiles);
        Assert.assertTrue(chunkFiles.size() > 0);

        contentChecksum = contentInputStream.getMessageDigest();
        Assert.assertNotNull(contentChecksum);
        Assert.assertTrue(contentChecksum.getDigestLength() > 0);
    }

    private void verifyTotalChunkLength() {
        long size = 0;
        for (File chunk : chunkFiles) {
            size += chunk.length();
        }

        Assert.assertEquals(contentSize, size);
    }

    private void verifyTotalChunkChecksum() throws Exception {
        MessageDigest md5 = MessageDigest.getInstance(Algorithm.MD5.name());
        DigestInputStream istream;
        for (File chunk : chunkFiles) {
            istream = new DigestInputStream(new FileInputStream(chunk), md5);
            read(istream);
            md5 = istream.getMessageDigest();
            IOUtils.closeQuietly(istream);
        }

        Assert.assertNotNull(md5);
        Assert.assertTrue(MessageDigest.isEqual(contentChecksum.digest(),
                                                md5.digest()));

    }

    private void read(DigestInputStream istream) throws IOException {
        while (istream.read() != -1) {
            // walk through the stream
        }
    }

    private DigestInputStream createContent(long size) throws IOException {
        contentFile = File.createTempFile(LARGE_PREFIX, ".txt");
        FileOutputStream out = new FileOutputStream(contentFile);

        int MIN_CHAR = 32;
        int MAX_CHAR_MINUS_MIN_CHAR = 126 - MIN_CHAR;
        Random r = new Random();
        for (long i = 0; i < size; ++i) {

            if (i % 101 == 0) {
                out.write("\n".getBytes());
            } else {
                out.write(r.nextInt(MAX_CHAR_MINUS_MIN_CHAR) + MIN_CHAR);
            }
        }
        IOUtils.closeQuietly(out);

        return ChecksumUtil.wrapStream(new FileInputStream(contentFile),
                                       Algorithm.MD5);
    }
}
