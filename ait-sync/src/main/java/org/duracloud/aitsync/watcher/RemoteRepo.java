package org.duracloud.aitsync.watcher;

import java.util.Date;
import java.util.List;

import org.duracloud.aitsync.service.RemoteRepoException;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/18/2012
 */
public interface RemoteRepo {
    /**
     * 
     * @param groupId
     * @param startDate
     * @return
     * @throws RemoteRepoException
     */
    public List<Resource> getResources(long groupId,
                                                Date startDate)
        throws RemoteRepoException;
}
