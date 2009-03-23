package org.duraspace.duradav.methods;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import org.duraspace.duradav.core.Body;
import org.duraspace.duradav.core.ConflictException;
import org.duraspace.duradav.core.Content;
import org.duraspace.duradav.core.ContentPath;
import org.duraspace.duradav.core.MethodNotAllowedException;
import org.duraspace.duradav.core.Resource;
import org.duraspace.duradav.core.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

/**
 * Handles PUT requests.
 */
class PutHandler implements MethodHandler {

    public void handleRequest(WebdavStore store,
                              Resource resource,
                              HttpServletRequest req,
                              HttpServletResponse resp) throws WebdavException {
        if (resource.isCollection()) {
            throw new MethodNotAllowedException(resource.getPath(),
                    "Collections don't support PUT -- try MKCOL");
        }

        if (!store.hasCollection(resource.getPath().getParent())) {
            throw new ConflictException(resource.getPath(),
                    "Parent collection does not exist");
        }

        boolean replaced;
        InputStream source = null;
        try {
            source = req.getInputStream();
            ContentPath contentPath = ((Content) resource).getContentPath();
            replaced = store.putContent(new Content(contentPath,
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
