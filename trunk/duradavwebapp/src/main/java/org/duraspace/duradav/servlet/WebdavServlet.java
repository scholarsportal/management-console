package org.duraspace.duradav.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.duraspace.duradav.core.Collection;
import org.duraspace.duradav.core.CollectionPath;
import org.duraspace.duradav.core.Content;
import org.duraspace.duradav.core.ContentPath;
import org.duraspace.duradav.core.MethodNotAllowedException;
import org.duraspace.duradav.core.NotFoundException;
import org.duraspace.duradav.core.Path;
import org.duraspace.duradav.core.Resource;
import org.duraspace.duradav.core.WebdavException;
import org.duraspace.duradav.methods.Method;
import org.duraspace.duradav.store.WebdavStore;
import org.duraspace.duradav.store.filesystem.FilesystemStore;

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
        store = new FilesystemStore(new File("/tmp/duradav"));
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
            logger.debug(logMessage, error);
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