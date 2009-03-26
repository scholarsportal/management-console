package org.duraspace.duradav.servlet.methods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duraspace.duradav.core.Collection;
import org.duraspace.duradav.core.Resource;
import org.duraspace.duradav.error.MethodNotAllowedException;
import org.duraspace.duradav.error.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

/**
 * Handles MKCOL requests.
 */
class MkColHandler implements MethodHandler {

    public void handleRequest(WebdavStore store,
                              Resource resource,
                              HttpServletRequest req,
                              HttpServletResponse resp) throws WebdavException {
        if (resource.isCollection()) {
            store.createCollection((Collection) resource);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            throw new MethodNotAllowedException(resource.getPath(),
                    "Path does not denote a collection");
        }
    }

}