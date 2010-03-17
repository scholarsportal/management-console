package org.duracloud.sync.mgmt;

import org.duracloud.sync.ChangedList;

import java.io.File;

/**
 * @author: Bill Branan
 * Date: Mar 17, 2010
 */
public class ChangeWatcher implements Runnable {

    private boolean continueWatch;
    private ChangedList changedList;
    private ChangeHandler handler;

    public ChangeWatcher(ChangedList changedList, ChangeHandler handler) {
        this.handler = handler;
        this.changedList = changedList; 
        continueWatch = true;
    }

    public void run() {
        while(continueWatch) {
            File changedFile = changedList.getChangedFile();
            if(changedFile != null) {
                handler.fileChanged(changedFile);
            }
        }
    }

    public void endWatch() {
        continueWatch = false;
    }
}
