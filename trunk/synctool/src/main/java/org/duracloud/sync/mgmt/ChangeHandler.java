package org.duracloud.sync.mgmt;

/**
  * @author: Bill Branan
 * Date: Mar 17, 2010
 */
public interface ChangeHandler {

    public void fileChanged(ChangedFile changedFile);
    
}
