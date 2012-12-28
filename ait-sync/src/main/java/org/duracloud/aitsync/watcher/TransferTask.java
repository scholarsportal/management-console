package org.duracloud.aitsync.watcher;

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
