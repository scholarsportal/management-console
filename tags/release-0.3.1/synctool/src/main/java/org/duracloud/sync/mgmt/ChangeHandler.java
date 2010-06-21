package org.duracloud.sync.mgmt;

/**
  * @author: Bill Branan
 * Date: Mar 17, 2010
 */
public interface ChangeHandler {

    /**
     * Tells the handler that a file has changed
     *
     * @param changedFile a file which has changed
     * @returns true if handling was successful, false otherwise
     */
    public boolean handleChangedFile(ChangedFile changedFile);

}
