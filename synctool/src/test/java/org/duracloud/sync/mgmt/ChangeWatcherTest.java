package org.duracloud.sync.mgmt;

import static junit.framework.Assert.assertEquals;
import org.duracloud.sync.SyncTestBase;
import org.junit.Test;

import java.io.File;

/**
 * @author: Bill Branan
 * Date: Mar 25, 2010
 */
public class ChangeWatcherTest extends SyncTestBase {

    private int changes;

    @Test
    public void testChangeWatcher() throws Exception {
        ChangeWatcher changeWatcher =
            new ChangeWatcher(changedList, new TestHandler(), 100);
        new Thread(changeWatcher).start();

        changes = 0;
        changedList.addChangedFile(new File("test-0"));
        Thread.sleep(200);
        assertEquals(1, changes);

        changes = 0;
        int changedFiles = 10;
        for(int i=0; i < changedFiles; i++) {
            changedList.addChangedFile(new File("test-file-" + i));
        }
        Thread.sleep(200);
        assertEquals(changedFiles, changes);

        changes = 0;
        changeWatcher.endWatch();
        changedList.addChangedFile(new File("test-3"));
        Thread.sleep(200);
        assertEquals(0, changes);        
    }

    private class TestHandler implements ChangeHandler {
        public void fileChanged(ChangedFile changedFile) {
            changes++;
        }
    }
}
