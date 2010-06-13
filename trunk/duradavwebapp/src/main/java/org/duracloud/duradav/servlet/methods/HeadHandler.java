/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradav.servlet.methods;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.duradav.core.Collection;
import org.duracloud.duradav.core.Content;
import org.duracloud.duradav.core.Resource;
import org.duracloud.duradav.error.WebdavException;
import org.duracloud.duradav.store.WebdavStore;

/**
 * Handles HEAD requests.
 */
class HeadHandler implements MethodHandler {

    public void handleRequest(WebdavStore store,
                              Resource resource,
                              HttpServletRequest req,
                              HttpServletResponse resp)
            throws WebdavException {
        if (resource.isCollection()) {
            setCollectionHeaders((Collection) resource, resp);
        } else {
            setContentHeaders((Content) resource, resp);
        }
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
        }
    }

    private static final void setCommonHeaders(Resource resource,
                                               HttpServletResponse resp) {
        Date date = resource.getModifiedDate();
        if (date != null) {
            resp.setDateHeader("Last-Modified", date.getTime());
        }
    }

}
