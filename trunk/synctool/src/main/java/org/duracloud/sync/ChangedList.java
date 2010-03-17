package org.duracloud.sync;

import java.io.File;
import java.util.HashMap;

/**
 * @author: Bill Branan
 * Date: Mar 15, 2010
 */
public class ChangedList {

    private HashMap<String, File> fileList;

    private static ChangedList instance;

    public static synchronized ChangedList getInstance() {
        if(instance == null) {
            instance = new ChangedList();
        }
        return instance;
    }

    private ChangedList() {
        fileList = new HashMap<String, File>();
    }

    /**
     * Adds a changed file to the list of items to be processed.
     * Note that only the most current update to any given file is
     * provided to the change processor.
     *
     * @param changedFile a file which has changed on the file system
     */
    public synchronized void addChangedFile(File changedFile) {
        fileList.put(changedFile.getAbsolutePath(), changedFile);
    }

    /**
     * Retrieves a changed file for processing and removes it from the list.
     * Returns null if there are no changed files in the list.
     *
     * @return a file which has changed on the file system
     */
    public synchronized File getChangedFile() {
        if(fileList.isEmpty()) {
            return null;
        }

        String key = fileList.keySet().iterator().next();
        return fileList.remove(key);
    }
}
