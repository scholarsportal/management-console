package org.duracloud.sync.monitor;

import org.apache.commons.io.monitor.FilesystemMonitor;
import org.apache.commons.io.monitor.FilesystemObserver;
import org.duracloud.sync.mgmt.SyncManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * @author: Bill Branan
 * Date: Mar 12, 2010
 */
public class DirectoryUpdateMonitor {

    private final Logger logger = LoggerFactory.getLogger(SyncManager.class);

    private FilesystemMonitor monitor;

    public DirectoryUpdateMonitor(List<File> directories, long pollFrequency) {
        monitor = new FilesystemMonitor(pollFrequency);

        for (File watchDir : directories) {
            if (watchDir.exists() && watchDir.isDirectory()) {
                FilesystemObserver observer = new FilesystemObserver(watchDir);
                observer.addListener(new DirectoryListener());
                monitor.addObserver(observer);
            } else {
                throw new RuntimeException("Path " +
                    watchDir.getAbsolutePath() +
                    " either does not exist or is not a directory");
            }
        }
    }

    public void startMonitor() {
        logger.info("Starting Directory Update Monitor");
        try {
            monitor.start();
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void stopMonitor() {
        try {
            monitor.stop();
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
