package org.duracloud.aitsync.repo;

import java.util.Date;
import java.util.List;


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
