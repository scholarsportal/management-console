package org.duracloud.duradav.servlet.methods;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.duradav.core.Resource;
import org.duracloud.duradav.error.WebdavException;
import org.duracloud.duradav.store.WebdavStore;

public interface MethodHandler {

    void handleRequest(WebdavStore store,
                       Resource resource,
                       HttpServletRequest req,
                       HttpServletResponse resp)
            throws WebdavException;

}
