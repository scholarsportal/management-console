/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradav.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.duracloud.duradav.core.Collection;
import org.duracloud.duradav.core.CollectionPath;
import org.duracloud.duradav.core.Content;
import org.duracloud.duradav.core.ContentPath;
import org.duracloud.duradav.core.Path;
import org.duracloud.duradav.core.Resource;
import org.duracloud.duradav.error.MethodNotAllowedException;
import org.duracloud.duradav.error.NotFoundException;
import org.duracloud.duradav.error.WebdavException;
import org.duracloud.duradav.servlet.methods.Method;
import org.duracloud.duradav.store.WebdavStore;
import org.duracloud.duradav.store.filesystem.FilesystemStore;

/**
 * Provides a WebDAV interface over a <code>WebdavStore</code> implementation.
 */
public class WebdavServlet extends HttpServlet {

    public static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory.getLogger(WebdavServlet.class);

    private WebdavStore store;

    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        // TODO: init impl from config
        store = new FilesystemStore(new File("/tmp/duradav-base"),
                                    new File("/tmp/duradav-temp"));
    }

    /**
     * Sends the request to the appropriate handler.
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        String methodName = null;
        Path path = null;

        try {
            methodName = req.getMethod();
            path = getPath(req);
            Method method = Method.fromName(methodName);
            if (method == null) {
                throw new MethodNotAllowedException(path,
                        "Method not supported: " + methodName);
            }
            logger.debug("Got " + method.getName() + " request for " + path);
            Resource resource = getResource(path,
                                            method.requiresExistingResource(),
                                            req,
                                            resp);
            method.getHandler().handleRequest(store,
                                              resource,
                                              req,
                                              resp);
        } catch (WebdavException e) {
            handleError(methodName, path, e, resp);
        } catch (Throwable th) {
            handleFault(methodName, path, th, resp);
        }
    }

    // sends Content-Location header if necessary
    private Resource getResource(Path path,
                                 boolean fromStore,
                                 HttpServletRequest req,
                                 HttpServletResponse resp)
            throws WebdavException {
        if (path.denotesCollection()) {
            CollectionPath collPath = (CollectionPath) path;
            if (fromStore) {
                return store.getCollection(collPath);
            }
            return new Collection(collPath, null, null);
        }

        ContentPath contPath = (ContentPath) path;
        if (fromStore) {
            try {
                return store.getContent(contPath);
            } catch (NotFoundException noSuchContent) {
                // recover if a collection exists at path + '/'
                CollectionPath collPath = new CollectionPath(path + "/");
                try {
                    Collection collection = store.getCollection(collPath);
                    resp.setHeader("Content-Location",
                                   req.getRequestURL() + "/");
                    return collection;
                } catch (NotFoundException noSuchCollection) {
                    throw noSuchContent;
                }
            }
        }
        return new Content(contPath, null, null, -1, null);
    }

    private static Path getPath(HttpServletRequest req) {
        String pathInfo = getPathInfo(req).trim();
        if (pathInfo.endsWith("/")) {
            return new CollectionPath(pathInfo);
        }
        return new ContentPath(pathInfo);
    }

    private static String getPathInfo(HttpServletRequest req) {
        // it should be here
        String pathInfo = req.getPathInfo();
        if (req.getPathInfo() == null) {
            // ...but Tomcat puts it here
            pathInfo = req.getServletPath();
        }
        return pathInfo;
    }

    private static void handleError(String methodName,
                                    Path path,
                                    WebdavException error,
                                    HttpServletResponse resp) {
        String logMessage = methodName + " request failed on " + path
                + " [" + error.getStatusCode() + "] - " + error.getMessage();
        if (logger.isDebugEnabled()) {
            logger.error(logMessage, error);
        } else {
            logger.error(logMessage);
        }
        try {
            resp.sendError(error.getStatusCode(), error.getMessage());
        } catch (IOException e) {
            logger.error("IO error while sending webdav failure reponse", e);
        }
    }

    private static void handleFault(String methodName,
                                    Path path,
                                    Throwable fault,
                                    HttpServletResponse resp) {
        logger.error("Fault while servicing " + methodName + " request on "
                     + path, fault);
        try {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                           fault.getMessage());
        } catch (IOException e) {
            logger.error("IO error while sending fault reponse", e);
        }
    }

}