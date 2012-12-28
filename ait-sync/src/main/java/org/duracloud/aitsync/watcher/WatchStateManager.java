package org.duracloud.aitsync.watcher;

import java.util.Date;


/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public interface WatchStateManager {
    /**
     * 
     * @param archiveItAccountId
     * @return
     */
    WatchState getState(long archiveItAccountId);

    /**
     * 
     * @param archiveItAccountId
     * @param date
     */
    void setDateOfLastCopiedResource(long archiveItAccountId, Date date);
    
    /**
     * 
     * @param archiveItAccountId
     */
    void removeState(long archiveItAccountId);
}
