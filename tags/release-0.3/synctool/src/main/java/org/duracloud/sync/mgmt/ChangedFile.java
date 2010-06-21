package org.duracloud.sync.mgmt;

import java.io.File;
import java.io.Serializable;

/**
 * @author: Bill Branan
 * Date: Apr 1, 2010
 */
public class ChangedFile implements Serializable {

    private File changedFile;
    private int syncAttempts;

    public ChangedFile(File changedFile) {
        this.changedFile = changedFile;
        syncAttempts = 0;
    }

    public File getFile() {
        return changedFile;
    }

    public int getSyncAttempts() {
        return syncAttempts;
    }

    public void incrementSyncAttempts() {
        syncAttempts++;
    }
}
