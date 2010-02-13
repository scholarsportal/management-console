package org.duracloud.chunk.writer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.duracloud.chunk.ChunkableContent;
import org.duracloud.chunk.error.NotFoundException;
import org.duracloud.chunk.manifest.ChunksManifest;
import org.duracloud.chunk.manifest.ChunksManifestBean;
import org.duracloud.chunk.stream.ChunkInputStream;
import org.duracloud.chunk.stream.KnownLengthInputStream;
import org.duracloud.common.util.ChecksumUtil;
import static org.duracloud.common.util.ChecksumUtil.Algorithm.MD5;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.util.Collection;
import java.util.List;

/**
 * @author Andrew Woods
 *         Date: Feb 5, 2010
 */
public class FilesystemContentWriterTest {

    private FilesystemContentWriter writer;

    private File testDir = new File("target/test-writer-fs");
    private File destDir = new File(testDir, "dest");

    @Before
    public void setUp() {
        if (!testDir.exists()) {
            Assert.assertTrue(testDir.mkdirs());
        }

        if (!destDir.exists()) {
            Assert.assertTrue(destDir.mkdirs());
        }

        writer = new FilesystemContentWriter();
    }

    @After
    public void tearDown() {
        writer = null;
    }

    @Test
    public void testWrite() {
        long contentSize = 4000;
        InputStream contentStream = createContentStream(contentSize);

        File destSpace = new File(destDir, "spaceId-0");
        String spaceId = destSpace.getPath();
        String contentId = "a/b/c/contentId";

        int numChunks = 4;
        long maxChunkSize = 1024;
        ChunkableContent chunkable = new ChunkableContent(contentId,
                                                          contentStream,
                                                          contentSize,
                                                          maxChunkSize);
        ChunksManifest manifest = writer.write(spaceId, chunkable);

        // check files
        IOFileFilter all = FileFilterUtils.trueFileFilter();
        Collection<File> files = FileUtils.listFiles(destSpace, all, all);
        Assert.assertNotNull(files);

        Assert.assertEquals(numChunks + 1/*manifest*/, files.size());

        for (File file : files) {
            String filePath = file.getPath();
            Assert.assertTrue(filePath, filePath.indexOf(contentId) != -1);
        }

        // check manifest
        Assert.assertNotNull(manifest);

        ChunksManifestBean.ManifestHeader header = manifest.getHeader();
        Assert.assertNotNull(header);

        List<ChunksManifestBean.ManifestEntry> entries = manifest.getEntries();
        Assert.assertNotNull(entries);
        Assert.assertEquals(numChunks, entries.size());

        KnownLengthInputStream body = manifest.getBody();
        Assert.assertNotNull(body);
        Assert.assertTrue(body.getLength() > 0);
    }

    private InputStream createContentStream(long size) {
        Assert.assertTrue("let's keep it reasonable", size < 10001);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (long i = 0; i < size; ++i) {
            if (i % 101 == 0) {
                out.write('\n');
            } else {
                out.write('a');
            }
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Test
    public void testWriteSingle() throws NotFoundException, IOException {
        long contentSize = 1500;
        InputStream contentStream = createContentStream(contentSize);

        String spaceId = new File(destDir, "spaceId-1").getPath();
        String contentId = "a/b/c/contentId";

        boolean preserveMD5 = true;
        ChunkInputStream chunk = new ChunkInputStream(contentId,
                                                      contentStream,
                                                      contentSize,
                                                      preserveMD5);

        String md5 = writer.writeSingle(spaceId, chunk);
        Assert.assertNotNull(md5);

        // check files
        File file = new File(spaceId, contentId);
        Assert.assertTrue(file.exists());

        FileInputStream fileStrm = new FileInputStream(file);
        DigestInputStream digestStrm = ChecksumUtil.wrapStream(fileStrm, MD5);
        read(digestStrm);
        String md5Real = ChecksumUtil.getChecksum(digestStrm);

        IOUtils.closeQuietly(digestStrm);
        IOUtils.closeQuietly(contentStream);

        Assert.assertEquals(md5Real, md5);

    }

    private void read(InputStream stream) throws IOException {
        while (stream.read() != -1) {
            // spin
        }
    }
}
