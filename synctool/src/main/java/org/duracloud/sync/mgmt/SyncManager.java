package org.duracloud.sync.mgmt;

import org.duracloud.sync.endpoint.SyncEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The SyncManager is responsible to watch for new entries in the ChangedList
 * and make sure those changes are pushed to the SyncEndpoint.
 *
 * @author: Bill Branan
 * Date: Mar 15, 2010
 */
public class SyncManager implements ChangeHandler {

    private final Logger logger = LoggerFactory.getLogger(SyncManager.class);

    private ChangeWatcher changeWatcher;
    private List<File> watchDirs;
    private SyncEndpoint endpoint;
    private ExecutorService execPool;

    /**
     * Creates a SyncManager which, when started, will watch for updates to
     * the ChangedList and kick off SyncWorkers to handle any changed files.
     *
     * @param endpoint
     * @param threads
     * @param frequency
     */
    public SyncManager(List<File> watchDirs,
                       SyncEndpoint endpoint,
                       int threads,
                       long frequency) {
        logger.info("Starting Sync Manager with " + threads + " threads");
        this.watchDirs = watchDirs;
        this.endpoint = endpoint;
        changeWatcher = new ChangeWatcher(ChangedList.getInstance(),
                                          this,
                                          frequency);

        // Create thread pool for workers and changeWatcher
        execPool = Executors.newFixedThreadPool(threads + 1);
    }

    /**
     * Allows the SyncManager to begin watching for updates to the ChangedList
     */
    public void beginSync() {
        execPool.execute(changeWatcher);
    }

    /**
     * Stops the sync, no further changed files will be handled after those
     * which are in progress have completed.
     */
    public void endSync() {
        logger.info("Closing Sync Manager, ending sync");
        changeWatcher.endWatch();
        execPool.shutdown();
    }

    /**
     * Notifies the SyncManager that a file has changed
     *
     * @param changedFile the changed file
     */
    public void fileChanged(File changedFile) {
        File watchDir = getWatchDir(changedFile);
        execPool.execute(new SyncWorker(changedFile, watchDir, endpoint));
    }

    /*
     * Determines which of the watched directories includes the changed file
     */
    protected File getWatchDir(File changedFile) {
        for(File watchDir : watchDirs) {
            File changedFileParent = changedFile.getParentFile();
            while(changedFileParent != null) {
                if(changedFileParent.equals(watchDir)) {
                    return watchDir;
                } else {
                    changedFileParent = changedFileParent.getParentFile();
                }
            }
        }
        throw new RuntimeException("File " + changedFile.getAbsolutePath() +
                                   " is not in any watched directory");
    }
}
