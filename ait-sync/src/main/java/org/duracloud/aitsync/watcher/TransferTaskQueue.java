package org.duracloud.aitsync.watcher;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public interface TransferTaskQueue<T extends TransferTask<?>> {
    void put(T transferTask) throws InterruptedException;
    T take() throws InterruptedException;
}
