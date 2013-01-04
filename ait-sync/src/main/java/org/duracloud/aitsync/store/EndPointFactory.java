package org.duracloud.aitsync.store;

import org.duracloud.aitsync.repo.Resource;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public interface EndPointFactory {
    public EndPoint createEndPoint(Resource resource) throws EndPointException;
}
