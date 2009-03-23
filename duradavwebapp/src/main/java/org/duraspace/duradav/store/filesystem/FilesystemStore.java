package org.duraspace.duradav.store.filesystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.duraspace.duradav.core.Body;
import org.duraspace.duradav.core.Collection;
import org.duraspace.duradav.core.CollectionPath;
import org.duraspace.duradav.core.Content;
import org.duraspace.duradav.core.ContentPath;
import org.duraspace.duradav.core.Path;
import org.duraspace.duradav.error.NotFoundException;
import org.duraspace.duradav.error.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

/**
 * WebdavStore implementation for a regular filesystem.
 */
public class FilesystemStore implements WebdavStore {

    private static final Logger logger = LoggerFactory.getLogger(FilesystemStore.class);

    private final File baseDir;

    public FilesystemStore(File baseDir) {
        this.baseDir = baseDir;
        if (!baseDir.exists()) {
            if (!baseDir.mkdirs()) {
                throw new RuntimeException("Error creating dir " + baseDir);
            }
        }
        logger.info("Initialized with baseDir: {}", baseDir.getPath());
    }

    /**
     * {@inheritDoc}
     */
    public Content getContent(ContentPath path) throws WebdavException {
        File file = getFile(path);
        if (!file.isFile()) {
            throw new NotFoundException(path);
        }
        return new Content(path,
                           new Date(),
                           Body.fromFile(file),
                           file.length(),
                           null);
    }

    /**
     * {@inheritDoc}
     */
    public Collection getCollection(CollectionPath path) throws WebdavException {
        final File dir = getFile(path);
        if (!dir.isDirectory()) {
            throw new NotFoundException(path);
        }

        // children will be lazily determined
        Iterable<String> children = new Iterable<String>() {
            private List<String> children;
            public Iterator<String> iterator() {
                if (children == null) {
                    children = new ArrayList<String>();
                    for (String name : dir.list()) {
                        File file = new File(dir, name);
                        if (file.isFile()) {
                            children.add(name);
                        } else {
                            children.add(name + "/");
                        }
                    }
                }
                return children.iterator();
            }
        };

        return new Collection(path, new Date(dir.lastModified()), children);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasCollection(CollectionPath path) {
        return getFile(path).isDirectory();
    }

    /**
     * {@inheritDoc}
     */
    public boolean putContent(Content content) {
        File file = getFile(content.getPath());
        boolean replaced = false;
        if (file.exists()) {
            replaced = true;
        }
        InputStream source = content.getBody().getStream();
        FileOutputStream sink = null;
        try {
            sink = new FileOutputStream(file);
            IOUtils.copy(source, sink);
            return replaced;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(source);
            IOUtils.closeQuietly(sink);
        }
    }

    private File getFile(Path path) {
        if (path.equals(Path.ROOT)) {
            return baseDir;
        }
        String abs = path.toString();
        int minus = 0;
        if (path instanceof CollectionPath) {
            minus = 1;
        }
        String rel = abs.substring(1, abs.length() - minus);
        return new File(baseDir, rel);
    }

}
