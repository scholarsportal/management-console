package org.duraspace.duradav.handler;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duraspace.duradav.core.Collection;
import org.duraspace.duradav.core.CollectionPath;
import org.duraspace.duradav.core.Content;
import org.duraspace.duradav.core.ContentPath;
import org.duraspace.duradav.core.NotFoundException;
import org.duraspace.duradav.core.Resource;
import org.duraspace.duradav.core.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

/**
 * Handles HEAD requests.
 */
public class HeadHandler
        implements Handler {

    private final WebdavStore store;

    public HeadHandler(WebdavStore store) {
        this.store = store;
    }

    /**
     * {@inheritDoc}
     */
    public void handleCollectionRequest(CollectionPath path,
                                        HttpServletRequest req,
                                        HttpServletResponse resp)
            throws WebdavException {
        setCollectionHeaders(store.getCollection(path), resp);
    }

    /**
     * {@inheritDoc}
     */
    public void handleContentRequest(ContentPath path,
                                     HttpServletRequest req,
                                     HttpServletResponse resp)
            throws WebdavException {
        Content content = null;
        try {
            content = store.getContent(path);
        } catch (NotFoundException e) {
            // ok...but is there a collection at path + '/'?
            CollectionPath collPath = new CollectionPath(path.toString() + "/");
            if (store.hasCollection(collPath)) {
                // yes; we can give them that
                resp.setHeader("Content-Location", req.getRequestURL() + "/");
                handleCollectionRequest(collPath, req, resp);
            } else {
                throw e;
            }
        }
        setContentHeaders(content, resp);
    }

    static final void setCollectionHeaders(Collection collection,
                                           HttpServletResponse resp) {
        setCommonHeaders(collection, resp);
        resp.setContentType(GetHandler.COLLECTION_CONTENT_TYPE);
    }

    static final void setContentHeaders(Content content,
                                        HttpServletResponse resp) {
        setCommonHeaders(content, resp);
        long length = content.getLength();
        if (length > 0) {
            resp.setHeader("Content-Length", "" + length);
        }
        String mediaType = content.getMediaType();
        if (mediaType != null) {
            resp.setContentType(mediaType);
        } // TODO: else guess based on extension?
    }

    private static final void setCommonHeaders(Resource resource,
                                               HttpServletResponse resp) {
        Date date = resource.getModifiedDate();
        if (date != null) {
            resp.setDateHeader("Last-Modified", date.getTime());
        }
    }

}
