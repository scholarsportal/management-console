package org.duracloud.aitsync.service;

import org.duracloud.aitsync.watcher.TransferTask;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public interface Worker {

    void pause();

    void resume();

    TransferTask getTransferTask();

}
