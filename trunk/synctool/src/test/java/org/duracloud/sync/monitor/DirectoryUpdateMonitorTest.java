package org.duracloud.sync.monitor;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertEquals;
import org.apache.commons.io.FileUtils;
import org.duracloud.sync.SyncTestBase;
import org.duracloud.sync.mgmt.ChangedFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Bill Branan
 * Date: Mar 25, 2010
 */
public class DirectoryUpdateMonitorTest extends SyncTestBase {

    private File tempDir;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        tempDir = createTempDir("monitor-dir");
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        FileUtils.deleteDirectory(tempDir);
    }    

    @Test
    public void testDirectoryUpdateMonitor() throws Exception {
        List<File> dirs = new ArrayList<File>();
        dirs.add(tempDir);

        DirectoryUpdateMonitor monitor = new DirectoryUpdateMonitor(dirs, 100);
        monitor.startMonitor();

        // Create file
        File tempFile = File.createTempFile("temp", "file", tempDir);
        checkFileInChangedList(tempFile);

        // Update file
        FileWriter writer = new FileWriter(tempFile);
        writer.write("test");
        writer.close();
        checkFileInChangedList(tempFile);

        // Delete file
        tempFile.delete();
        checkFileInChangedList(tempFile);

        monitor.stopMonitor();
    }

    private void checkFileInChangedList(File file) throws Exception {
        Thread.sleep(1000);
        ChangedFile changedFile = changedList.getChangedFile();
        assertNotNull(changedFile);
        assertEquals(file.getAbsolutePath(), 
                     changedFile.getFile().getAbsolutePath());
        assertNull(changedList.getChangedFile());
    }
}
