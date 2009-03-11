package org.duraspace.duradav.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duraspace.duradav.core.CollectionPath;
import org.duraspace.duradav.core.ContentPath;
import org.duraspace.duradav.core.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

/**
 * Handles OPTIONS requests.
 */
public class OptionsHandler
        implements Handler {

    private final WebdavStore store;

    public OptionsHandler(WebdavStore store) {
        this.store = store;
    }

    /**
     * {@inheritDoc}
     */
    public void handleCollectionRequest(CollectionPath path,
                                        HttpServletRequest req,
                                        HttpServletResponse resp)
            throws WebdavException {
        resp.setHeader("DAV", "1");
        // TODO: finish impl
    }

    /**
     * {@inheritDoc}
     */
    public void handleContentRequest(ContentPath path,
                                     HttpServletRequest req,
                                     HttpServletResponse resp)
            throws WebdavException {
        resp.setHeader("DAV", "1");
        // TODO: finish impl
    }

}
