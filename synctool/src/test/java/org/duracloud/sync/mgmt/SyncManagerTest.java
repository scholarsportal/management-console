package org.duracloud.sync.mgmt;

import static junit.framework.Assert.assertEquals;
import org.duracloud.sync.SyncTestBase;
import org.duracloud.sync.endpoint.SyncEndpoint;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;

/**
 * @author: Bill Branan
 * Date: Mar 25, 2010
 */
public class SyncManagerTest extends SyncTestBase {

    private int handledFiles;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        handledFiles = 0;
    }

    @Test
    public void testSyncManager() throws Exception {
        SyncManager syncManager = new SyncManager(new TestEndpoint(), 2, 100);
        syncManager.beginSync();

        int changedFiles = 10;
        for(int i=0; i < changedFiles; i++) {
            changedList.addChangedFile(new File("test-file-" + i));
        }
        Thread.sleep(200);
        assertEquals(changedFiles, handledFiles);

        syncManager.endSync();
    }

    private class TestEndpoint implements SyncEndpoint {
        public void syncFile(File file) {
            handledFiles++;
        }

        public Iterator<String> getFilesList() {
            return null;
        }
    }
}
