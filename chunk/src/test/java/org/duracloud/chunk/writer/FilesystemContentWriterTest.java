package org.duracloud.chunk.writer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.duracloud.chunk.ChunkableContent;
import org.duracloud.chunk.manifest.ChunksManifest;
import org.duracloud.chunk.manifest.ChunksManifestBean;
import org.duracloud.chunk.stream.KnownLengthInputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
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

        String spaceId = new File(destDir, "spaceId-0").getPath();
        String contentId = "a/b/c/contentId";

        // If the max is on even boundaries with the content, an empty final
        //  chunk is created.
        int numChunks = 4;
        long maxChunkSize = 1024;
        ChunkableContent chunkable = new ChunkableContent(contentId,
                                                          contentStream,
                                                          contentSize,
                                                          maxChunkSize);
        ChunksManifest manifest = writer.write(spaceId, chunkable);

        // check files
        IOFileFilter all = FileFilterUtils.trueFileFilter();
        Collection<File> files = FileUtils.listFiles(destDir, all, all);
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
}
