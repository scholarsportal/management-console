package org.duraspace.duradav.methods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duraspace.duradav.core.Resource;
import org.duraspace.duradav.core.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

/**
 * Handles MKCOL requests.
 */
class MkColHandler implements MethodHandler {

    public void handleRequest(WebdavStore store,
                              Resource resource,
                              HttpServletRequest req,
                              HttpServletResponse resp) throws WebdavException {
        throw new RuntimeException("Not implemented");
    }

}