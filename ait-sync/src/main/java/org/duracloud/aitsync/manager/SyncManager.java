package org.duracloud.aitsync.manager;

public interface SyncManager {
    public static enum State {
        UNITIALIZED,
        READY, 
        RUNNING,
        PAUSED;
    }
    
    State getState();
    /**
     * 
     */
    void start();

    /**
     * 
     */
    void stop();
    
    void pause();
    
    void resume();
}
