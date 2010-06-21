package org.duracloud.duradav.servlet.methods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.duradav.core.Resource;
import org.duracloud.duradav.store.WebdavStore;

/**
 * Handles OPTIONS requests.
 */
class OptionsHandler implements MethodHandler {

    public void handleRequest(WebdavStore store,
                              Resource resource,
                              HttpServletRequest req,
                              HttpServletResponse resp) {
        resp.setHeader("DAV", "1");
    }

}
