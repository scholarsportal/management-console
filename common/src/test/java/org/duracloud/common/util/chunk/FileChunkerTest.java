package org.duracloud.common.util.chunk;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * @author Andrew Woods
 *         Date: Feb 4, 2010
 */
public class FileChunkerTest {

    private ContentWriter writer;

    private File testDir = new File("target/test-filechunker");
    private File srcDir = new File(testDir, "src");
    private File destDir = new File(testDir, "dest");

    @Before
    public void setUp() {
        if (!testDir.exists()) {
            Assert.assertTrue(testDir.mkdirs());
        }

        if (!destDir.exists()) {
            Assert.assertTrue(destDir.mkdirs());
        }

        if (!srcDir.exists()) {
            Assert.assertTrue(srcDir.mkdirs());
        }

        writer = new FilesystemContentWriter();
    }

    @Test
    public void testCreateContent() throws IOException {
        long chunkSize = 10240;
        long size = chunkSize * 4 + chunkSize / 2;
        String name = "create-test.txt";
        FileChunker chunker = new FileChunker(writer, chunkSize);
        createAndVerifyContent(chunker, name, size);
    }

    private File createAndVerifyContent(FileChunker chunker,
                                        String contentName,
                                        long contentSize) throws IOException {
        File outFile = new File(srcDir, contentName);
        chunker.createTestContent(outFile, contentSize);

        Assert.assertTrue(outFile.exists());
        Assert.assertEquals(contentSize, outFile.length());

        return outFile;
    }

    @Test
    public void testLoadContent() throws IOException {
        long chunkSize = 16384;
        long contentSize = chunkSize * 4 + chunkSize / 2;

        // test 0
        int runNumber = 0;
        doTestLoadContent(runNumber, contentSize, chunkSize);

        // test 1
        runNumber++;
        doTestLoadContent(runNumber, contentSize, chunkSize / 2);

        // test 2
        runNumber++;
        doTestLoadContent(runNumber, contentSize, chunkSize / 16);
    }

    private IOFileFilter doTestLoadContent(int runNumber,
                                           long contentSize,
                                           long chunkSize) throws IOException {
        String prefix = "load-test";
        String ext = ".txt";

        FileChunker chunker = new FileChunker(writer, chunkSize);

        String name = prefix + runNumber + ext;
        File content = createAndVerifyContent(chunker, name, contentSize);
        chunker.addContentFrom(srcDir,
                               destDir.getPath(),
                               FileFilterUtils.nameFileFilter(name));

        Pattern p = Pattern.compile(".*" + prefix + runNumber + ext + "-\\d+");
        IOFileFilter filter = new RegexFileFilter(p);
        IOFileFilter all = FileFilterUtils.trueFileFilter();
        Collection<File> files = FileUtils.listFiles(destDir, filter, all);

        Assert.assertNotNull(files);
        Assert.assertEquals(contentSize / chunkSize + 1, files.size());

        long totalChunksSize = 0;
        for (File file : files) {
            totalChunksSize += file.length();
        }
        Assert.assertEquals(content.length(), totalChunksSize);
        return all;
    }

}
