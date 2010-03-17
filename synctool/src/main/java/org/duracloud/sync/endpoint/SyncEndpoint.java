package org.duracloud.sync.endpoint;

import java.io.File;

/**
 * @author: Bill Branan
 * Date: Mar 17, 2010
 */
public interface SyncEndpoint {

    public void syncFile(File file);

}
