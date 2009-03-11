package org.duraspace.duradav.handler;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import org.duraspace.duradav.core.Body;
import org.duraspace.duradav.core.CollectionPath;
import org.duraspace.duradav.core.ConflictException;
import org.duraspace.duradav.core.Content;
import org.duraspace.duradav.core.ContentPath;
import org.duraspace.duradav.core.MethodNotAllowedException;
import org.duraspace.duradav.core.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

/**
 * Handles PUT requests.
 */
public class PutHandler
        implements Handler {

    private final WebdavStore store;

    public PutHandler(WebdavStore store) {
        this.store = store;
    }

    /**
     * {@inheritDoc}
     */
    public void handleCollectionRequest(CollectionPath path,
                                        HttpServletRequest req,
                                        HttpServletResponse resp)
            throws WebdavException {
        throw new MethodNotAllowedException(path,
                "Collections don't support PUT -- try MKCOL");
    }

    /**
     * {@inheritDoc}
     */
    public void handleContentRequest(ContentPath path,
                                     HttpServletRequest req,
                                     HttpServletResponse resp)
            throws WebdavException {
        if (!store.hasCollection(path.getParent())) {
            throw new ConflictException(path,
                                        "Parent collection does not exist");
        }

        boolean replaced;
        InputStream source = null;
        try {
            source = req.getInputStream();
            replaced = store.putContent(new Content(path,
                                                    null,
                                                    Body.fromStream(source),
                                                    getLength(req),
                                                    req.getContentType()));
        } catch (IOException e) {
            throw new RuntimeException("Error writing content", e);
        } finally {
            IOUtils.closeQuietly(source);
        }

        if (replaced) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            // TODO: According to webdav-servlet, the "Transmit" WebDAV client
            //        dies on this; needs a SC_NO_CONTENT status instead?!
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }
    }

    // TODO: move to a common place
    private static long getLength(HttpServletRequest req) {
        try {
            String string = req.getHeader("Content-Length");
            if (string == null) return -1;
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
