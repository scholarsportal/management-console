package org.duraspace.duradav.servlet.methods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duraspace.duradav.core.Collection;
import org.duraspace.duradav.core.Content;
import org.duraspace.duradav.core.Resource;
import org.duraspace.duradav.error.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

/**
 * Handles DELETE requests.
 */
class DeleteHandler implements MethodHandler {

    public void handleRequest(WebdavStore store,
                              Resource resource,
                              HttpServletRequest req,
                              HttpServletResponse resp) throws WebdavException {
        if (resource.isCollection()) {
            Collection collection = (Collection) resource;
            store.deleteCollection(collection.getCollectionPath());
        } else {
            Content content = (Content) resource;
            store.deleteContent(content.getContentPath());
        }
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

}