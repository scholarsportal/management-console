package org.duraspace.duradav.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duraspace.duradav.core.CollectionPath;
import org.duraspace.duradav.core.ContentPath;
import org.duraspace.duradav.core.WebdavException;

public interface Handler {

    void handleCollectionRequest(CollectionPath path,
                                 HttpServletRequest req,
                                 HttpServletResponse resp)
            throws WebdavException;

    void handleContentRequest(ContentPath path,
                              HttpServletRequest req,
                              HttpServletResponse resp)
            throws WebdavException;

}
