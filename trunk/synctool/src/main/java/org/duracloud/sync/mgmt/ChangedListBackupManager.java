package org.duracloud.sync.mgmt;

import org.duracloud.sync.util.DirectoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Manages the backing up of the changed list on a consistent schedule.
 *
 * @author: Bill Branan
 * Date: Mar 19, 2010
 */
public class ChangedListBackupManager implements Runnable {

    private final Logger logger =
        LoggerFactory.getLogger(ChangedListBackupManager.class);

    private File backupDir;
    private long backupFrequency;
    private ChangedList changedList;
    private boolean continueBackup;
    private long changedListVersion;

    public ChangedListBackupManager(ChangedList changedList,
                                    File backupDir,
                                    long backupFrequency) {
        this.backupDir = new File(backupDir, "changeList");
        if(!this.backupDir.exists()) {
            this.backupDir.mkdir();
        }

        this.backupFrequency = backupFrequency;
        this.changedList = changedList;

        continueBackup = true;
    }

    public void run() {
        while(continueBackup) {
            if(changedListVersion < changedList.getVersion()) {
                cleanupBackupDir(3);
                String filename = String.valueOf(System.currentTimeMillis());
                File persistFile = new File(backupDir, filename);
                changedListVersion = changedList.persist(persistFile);
            }

            try {
                Thread.sleep(backupFrequency);
            } catch(InterruptedException e) {
                logger.warn("ChangedListBackupManager thread interrupted");
            }
        }
    }

    /*
     * Removes all but the most recent backup files
     */
    private void cleanupBackupDir(int keep) {
        File[] backupDirFiles =
            DirectoryUtil.listFilesSortedByModDate(backupDir);
        if(backupDirFiles.length > keep) {
            for(int i=keep; i<backupDirFiles.length; i++) {
                backupDirFiles[i].delete();
            }
        }
    }

    public void endBackup() {
        continueBackup = false;
    }    
}
