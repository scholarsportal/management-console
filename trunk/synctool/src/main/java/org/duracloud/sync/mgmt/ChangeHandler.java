package org.duracloud.sync.mgmt;

import java.io.File;

/**
  * @author: Bill Branan
 * Date: Mar 17, 2010
 */
public interface ChangeHandler {

    public void fileChanged(File changedFile);
    
}
