package org.duraspace.duradav.servlet.methods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duraspace.duradav.core.Resource;
import org.duraspace.duradav.error.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

public interface MethodHandler {

    void handleRequest(WebdavStore store,
                       Resource resource,
                       HttpServletRequest req,
                       HttpServletResponse resp)
            throws WebdavException;

}
