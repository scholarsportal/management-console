package org.duraspace.duradav.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duraspace.duradav.core.CollectionPath;
import org.duraspace.duradav.core.ContentPath;
import org.duraspace.duradav.core.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

/**
 * Handles PROPPATCH requests.
 */
public class PropPatchHandler
        implements Handler {

    private final WebdavStore store;

    public PropPatchHandler(WebdavStore store) {
        this.store = store;
    }

    /**
     * {@inheritDoc}
     */
    public void handleCollectionRequest(CollectionPath path,
                                        HttpServletRequest req,
                                        HttpServletResponse resp)
            throws WebdavException {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    public void handleContentRequest(ContentPath path,
                                     HttpServletRequest req,
                                     HttpServletResponse resp)
            throws WebdavException {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

}
