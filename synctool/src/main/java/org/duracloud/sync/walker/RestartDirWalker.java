package org.duracloud.sync.walker;

import org.duracloud.sync.walker.DirWalker;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Walks a set of directory trees just like a DirWalker, but only adds files
 * to the changed list if their modified date is more recent than the time of
 * the last backup. This provides a listing of files which have been added or
 * updated since the last backup.
 *
 * @author: Bill Branan
 * Date: Mar 24, 2010
 */
public class RestartDirWalker extends DirWalker {

    private long lastBackup;

    public RestartDirWalker(List<File> topDirs, long lastBackup) {
        super(topDirs);
        this.lastBackup = lastBackup;
    }

    protected void handleFile(File file, int depth, Collection results) {
        if(file.lastModified() > lastBackup) {
            super.handleFile(file, depth, results);
        }
    }

}