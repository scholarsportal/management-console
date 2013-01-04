package org.duracloud.aitsync.queue;

import org.duracloud.aitsync.repo.Resource;
import org.duracloud.aitsync.store.EndPoint;

/**
 * 
 * @author Daniel Bernstein Date: 12/24/2012
 * 
 */
public interface TransferTask<T extends Resource> {
    T getResource();

    Long getQueueId();

    int getTries();

    void pause();

    void resume();

    void transferTo(EndPoint endPoint);

}
