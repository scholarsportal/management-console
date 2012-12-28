package org.duracloud.aitsync.service;

import org.duracloud.aitsync.watcher.TransferTask;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public interface WorkManager {
    /**
     * 
     * @param transferTask
     */
    void perform(TransferTask transferTask);

    /**
     * 
     */
    
    void start();
    
    /**
     * 
     */
    void shutdown();
    
    /**
     * 
     */
    void resume();
    
    /**
     * 
     */
    void pause();
    
    
}
