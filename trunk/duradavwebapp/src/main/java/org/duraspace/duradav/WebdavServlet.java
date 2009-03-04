package org.duraspace.duradav;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebdavServlet extends HttpServlet {

    public static final long serialVersionUID = 1L;

    private WebdavHandler handler;

    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        // TODO: init handler based on config
        handler = new DuraspaceWebdavHandler();
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
                throw new WebdavException("Unrecognized method: " + method);
            }
        } catch (WebdavException e) {
            handleError(e, resp);
        } catch (Throwable th) {
            handleFault(th, resp);
        }
    }

    private static void handleError(WebdavException error,
                                    HttpServletResponse resp) {
        // TODO: log as INFO and send appropriate response
        try {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, error.getMessage());
        } catch (IOException e) {
            // TODO: log as ERROR
        }
    }

    private static void handleFault(Throwable fault,
                                    HttpServletResponse resp) {
        // TODO: log as ERROR and send appropriate response
        try {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                           fault.getMessage());
        } catch (IOException e) {
            // TODO: log as ERROR
        }
    }

}