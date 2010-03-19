package org.duracloud.sync.mgmt;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.duracloud.sync.mgmt.ChangedList;

import java.io.File;


/**
 * @author: Bill Branan
 * Date: Mar 19, 2010
 */
public class ChangedListTest {

    private ChangedList changedList;
    private File changedFile;

    @Before
    public void setUp() throws Exception {
        changedList = ChangedList.getInstance();
        assertNull(changedList.getChangedFile());

        changedFile = File.createTempFile("changed", "file");
    }

    @After
    public void tearDown() throws Exception {
        assertNull(changedList.getChangedFile());
        changedFile.delete();
    }

    @Test
    public void testChangedList() throws Exception {
        long version = changedList.getVersion();
        changedList.addChangedFile(changedFile);
        assertEquals(version + 1, changedList.getVersion());

        File retrievedFile = changedList.getChangedFile();
        assertEquals(changedFile.getAbsolutePath(),
                     retrievedFile.getAbsolutePath());
        assertEquals(version + 2, changedList.getVersion());        
    }

    @Test
    public void testChangedListPersist() throws Exception {
        changedList.addChangedFile(changedFile);

        File persistFile = File.createTempFile("persist", "file");
        changedList.persist(persistFile);

        File retrievedFile = changedList.getChangedFile();
        assertEquals(changedFile.getAbsolutePath(),
                     retrievedFile.getAbsolutePath());
        assertNull(changedList.getChangedFile());

        changedList.restore(persistFile);

        retrievedFile = changedList.getChangedFile();
        assertEquals(changedFile.getAbsolutePath(),
                     retrievedFile.getAbsolutePath());
        assertNull(changedList.getChangedFile());

        persistFile.delete();
    }
}
