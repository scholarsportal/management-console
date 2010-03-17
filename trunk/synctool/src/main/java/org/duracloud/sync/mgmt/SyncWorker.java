package org.duracloud.sync.mgmt;

import org.duracloud.sync.endpoint.SyncEndpoint;

import java.io.File;

/**
 * @author: Bill Branan
 * Date: Mar 15, 2010
 */
public class SyncWorker implements Runnable {

    private File syncFile;
    private SyncEndpoint syncEndpoint;

    public SyncWorker(File file, SyncEndpoint endpoint) {
        this.syncFile = file;
        this.syncEndpoint = endpoint;
    }

    public void run() {
        syncEndpoint.syncFile(syncFile);
    }
}
