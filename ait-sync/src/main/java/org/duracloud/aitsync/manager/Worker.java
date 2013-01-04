package org.duracloud.aitsync.manager;

import org.duracloud.aitsync.queue.TransferTask;

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
