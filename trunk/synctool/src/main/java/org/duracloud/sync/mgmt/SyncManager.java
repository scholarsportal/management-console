package org.duracloud.sync.mgmt;

import org.duracloud.sync.endpoint.DuraStoreSyncEndpoint;
import org.duracloud.sync.endpoint.SyncEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Bill Branan
 * Date: Mar 15, 2010
 */
public class SyncManager implements ChangeHandler {

    private final Logger logger = LoggerFactory.getLogger(SyncManager.class);

    private ChangeWatcher changeWatcher;
    private ChangedListBackupManager backupManager;
    private SyncEndpoint endpoint;
    private ExecutorService execPool;

    public SyncManager(int threads, File backupDir, long frequency) {
        logger.info("Starting Sync Manager with " + threads + " threads");
        endpoint = new DuraStoreSyncEndpoint();
        changeWatcher = new ChangeWatcher(ChangedList.getInstance(),
                                          this,
                                          frequency);
        backupManager = new ChangedListBackupManager(ChangedList.getInstance(),
                                                     backupDir,
                                                     frequency);
        
        // Create thread pool for workers, changeWatcher, and backupManager
        execPool = Executors.newFixedThreadPool(threads + 2);
    }

    public void beginSync() {
        execPool.execute(changeWatcher);
        execPool.execute(backupManager);
    }

    public void endSync() {
        logger.info("Closing Sync Manager, ending sync");
        changeWatcher.endWatch();
        backupManager.endBackup();
        execPool.shutdown();
    }

    public void fileChanged(File changedFile) {
        execPool.execute(new SyncWorker(changedFile, endpoint));
    }    
}
