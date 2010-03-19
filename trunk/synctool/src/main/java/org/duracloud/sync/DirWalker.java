package org.duracloud.sync;

import org.apache.commons.io.DirectoryWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.duracloud.sync.mgmt.ChangedList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author: Bill Branan
 * Date: Mar 17, 2010
 */
public class DirWalker extends DirectoryWalker {

    private final Logger logger = LoggerFactory.getLogger(DirWalker.class);

    private List<File> topDirs;
    private ChangedList fileList;

    public DirWalker(List<File> topDirs) {
        super();
        this.topDirs = topDirs;
        fileList = ChangedList.getInstance();
    }

    public void walkDirs() {
        for(File dir : topDirs) {
            if(dir.exists() && dir.isDirectory()) {
                try {
                    List results = new ArrayList();
                    walk(dir, results);
                } catch(IOException e) {
                    throw new RuntimeException("Error walking directory " +
                        dir.getAbsolutePath() + ":" + e.getMessage(), e);
                }
            } else {
                logger.warn("Skipping " + dir.getAbsolutePath() +
                            ", as it does not point to a directory");
            }
        }
    }

    protected void handleFile(File file, int depth, Collection results) {
        fileList.addChangedFile(file);
    }

}
