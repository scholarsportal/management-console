package org.duracloud.sync.mgmt;

import org.duracloud.sync.endpoint.SyncEndpoint;

import java.io.File;

/**
 * Handles the syncing of a single changed file using the given endpoint.
 *
 * @author: Bill Branan
 * Date: Mar 15, 2010
 */
public class SyncWorker implements Runnable {

    private File syncFile;
    private SyncEndpoint syncEndpoint;

    /**
     * Creates a SyncWorker to handle syncing a file
     *
     * @param file the file to sync
     * @param endpoint the endpoint to which the file should be synced
     */
    public SyncWorker(File file, SyncEndpoint endpoint) {
        this.syncFile = file;
        this.syncEndpoint = endpoint;
    }

    public void run() {
        syncEndpoint.syncFile(syncFile);
    }
}
