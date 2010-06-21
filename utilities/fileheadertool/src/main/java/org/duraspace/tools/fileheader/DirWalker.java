package org.duraspace.tools.fileheader;

import org.apache.commons.io.DirectoryWalker;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/*
 * Handles the walking of a directory trees.
 *
 * @author: Bill Branan
 * Date: June 11, 2010
 */
public class DirWalker extends DirectoryWalker {

    private File topDir;
    private FileHandler handler;

    protected DirWalker(File topDir, FileHandler handler) {
        super();
        this.topDir = topDir;
        this.handler = handler;
    }

    public void walkDirs() {
        try {
            List results = new ArrayList();
            walk(topDir, results);
        } catch(IOException e) {
            throw new RuntimeException("Error walking directory " +
                topDir.getAbsolutePath() + ":" + e.getMessage(), e);
        }
    }

    @Override
    protected void handleFile(File file, int depth, Collection results) {
        handler.handleFile(file);
    }

}
