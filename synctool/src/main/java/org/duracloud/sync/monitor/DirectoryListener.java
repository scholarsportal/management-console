package org.duracloud.sync.monitor;

import org.apache.commons.io.monitor.FilesystemListenerAdaptor;
import org.duracloud.sync.ChangedList;

import java.io.File;

/**
 * @author: Bill Branan
 * Date: Mar 12, 2010
 */
public class DirectoryListener extends FilesystemListenerAdaptor {

    private ChangedList changedList;

    public DirectoryListener() {
        changedList = ChangedList.getInstance();
    }

    @Override
    public void onFileCreate(File file) {
        addFileToChangedList(file);
    }

    @Override
    public void onFileChange(File file) {
        addFileToChangedList(file);
    }

    @Override
    public void onFileDelete(File file) {
        addFileToChangedList(file);
    }

    private void addFileToChangedList(File file) {
        changedList.addChangedFile(file);
    }

}
