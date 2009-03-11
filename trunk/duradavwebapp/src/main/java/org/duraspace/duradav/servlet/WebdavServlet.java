package org.duraspace.duradav.servlet;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.duraspace.duradav.core.BadRequestException;
import org.duraspace.duradav.core.CollectionPath;
import org.duraspace.duradav.core.ContentPath;
import org.duraspace.duradav.core.Path;
import org.duraspace.duradav.core.WebdavException;
import org.duraspace.duradav.handler.GetHandler;
import org.duraspace.duradav.handler.Handler;
import org.duraspace.duradav.handler.HeadHandler;
import org.duraspace.duradav.handler.MkColHandler;
import org.duraspace.duradav.handler.MoveHandler;
import org.duraspace.duradav.handler.OptionsHandler;
import org.duraspace.duradav.handler.PropFindHandler;
import org.duraspace.duradav.handler.PropPatchHandler;
import org.duraspace.duradav.handler.PutHandler;
import org.duraspace.duradav.store.WebdavStore;
import org.duraspace.duradav.store.filesystem.FilesystemStore;

/**
 * Provides a WebDAV interface over a <code>WebdavStore</code> implementation.
 */
public class WebdavServlet extends HttpServlet {

    public static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory.getLogger(WebdavServlet.class);

    private final Map<String, Handler> handlers = new HashMap<String, Handler>();

    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        // TODO: init impl from config
        WebdavStore store = new FilesystemStore(new File("/tmp/duradav"));

        // register a handler for each recognized method
        handlers.put("GET", new GetHandler(store));
        handlers.put("HEAD", new HeadHandler(store));
        handlers.put("MOVE", new MoveHandler(store));
        handlers.put("MKCOL", new MkColHandler(store));
        handlers.put("OPTIONS", new OptionsHandler(store));
        handlers.put("PUT", new PutHandler(store));
        handlers.put("PROPFIND", new PropFindHandler(store));
        handlers.put("PROPPATCH", new PropPatchHandler(store));
    }

    /**
     * Dispatches a request to the appropriate handler.
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        String method = null;
        Path path = null;

        try {
            method = req.getMethod();
            path = getPath(req);
            handleRequest(handlers.get(method), path, req, resp);
        } catch (WebdavException e) {
            handleError(method, path, e, resp);
        } catch (Throwable th) {
            handleFault(method, path, th, resp);
        }
    }

    private static void handleError(String method,
                                    Path path,
                                    WebdavException error,
                                    HttpServletResponse resp) {
        String logMessage = method + " request failed on " + path
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

    private static void handleFault(String method,
                                    Path path,
                                    Throwable fault,
                                    HttpServletResponse resp) {
        logger.error("Fault while servicing " + method + " request on " + path,
                     fault);
        try {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                           fault.getMessage());
        } catch (IOException e) {
            logger.error("IO error while sending fault reponse", e);
        }
    }

    private static void handleRequest(Handler handler,
                                      Path path,
                                      HttpServletRequest req,
                                      HttpServletResponse resp)
            throws WebdavException {
        if (handler == null) {
            throw new BadRequestException(path, "Method not supported");
        }
        if (path instanceof CollectionPath) {
            handler.handleCollectionRequest((CollectionPath) path, req, resp);
        } else {
            handler.handleContentRequest((ContentPath) path, req, resp);
        }
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

}