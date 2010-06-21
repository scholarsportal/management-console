package org.duracloud.duradav.servlet.methods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.duradav.core.Collection;
import org.duracloud.duradav.core.Resource;
import org.duracloud.duradav.error.MethodNotAllowedException;
import org.duracloud.duradav.error.WebdavException;
import org.duracloud.duradav.store.WebdavStore;

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