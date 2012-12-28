package org.duracloud.aitsync.service;

import org.duracloud.aitsync.watcher.EndPoint;
import org.duracloud.aitsync.watcher.EndPointException;
import org.duracloud.aitsync.watcher.Resource;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public interface EndPointFactory {
    public EndPoint createEndPoint(Resource resource) throws EndPointException;
}
