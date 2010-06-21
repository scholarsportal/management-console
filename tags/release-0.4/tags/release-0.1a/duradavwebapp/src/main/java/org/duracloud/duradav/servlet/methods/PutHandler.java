package org.duracloud.duradav.servlet.methods;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import org.duracloud.duradav.core.Body;
import org.duracloud.duradav.core.Content;
import org.duracloud.duradav.core.ContentPath;
import org.duracloud.duradav.core.Resource;
import org.duracloud.duradav.error.ConflictException;
import org.duracloud.duradav.error.MethodNotAllowedException;
import org.duracloud.duradav.error.WebdavException;
import org.duracloud.duradav.store.WebdavStore;

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
                                                    Helper.getLength(req),
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



}
