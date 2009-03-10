package org.duraspace.duradav;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebdavServlet extends HttpServlet {

    public static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory.getLogger(WebdavServlet.class);

    private WebdavHandler handler;

    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        // TODO: init handler based on config
        handler = new DuraspaceWebdavHandler(new File("/tmp/duradav"));
    }

    /**
     * Entry point for HTTP requests.
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String method = req.getMethod();

        try {
            if (method.equals("GET")) {
                handler.handleGet(req, resp);
            } else if (method.equals("HEAD")) {
                handler.handleHead(req, resp);
            } else if (method.equals("MOVE")) {
                handler.handleMove(req, resp);
            } else if (method.equals("MKCOL")) {
                handler.handleMkCol(req, resp);
            } else if (method.equals("OPTIONS")) {
                handler.handleOptions(req, resp);
            } else if (method.equals("PUT")) {
                handler.handlePut(req, resp);
            } else if (method.equals("PROPFIND")) {
                handler.handlePropFind(req, resp);
            } else if (method.equals("PROPPATCH")) {
                handler.handlePropPatch(req, resp);
            } else {
                throw new WebdavException("Unrecognized method: " + method,
                                          HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (WebdavException e) {
            handleError(e, resp);
        } catch (Throwable th) {
            handleFault(th, resp);
        }
    }

    private static void handleError(WebdavException error,
                                    HttpServletResponse resp) {
        String logMessage = "Request failed [" + error.getStatusCode() + "] - "
            + error.getMessage();
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

    private static void handleFault(Throwable fault,
                                    HttpServletResponse resp) {
        logger.error("Fault while servicing webdav request", fault);
        try {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                           fault.getMessage());
        } catch (IOException e) {
            logger.error("IO error while sending fault reponse", e);
        }
    }

}