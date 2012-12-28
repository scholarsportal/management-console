package org.duracloud.aitsync.watcher;

import java.util.Date;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public interface WatchState {
    
    Date getDateOfLastCopiedResource();

}
