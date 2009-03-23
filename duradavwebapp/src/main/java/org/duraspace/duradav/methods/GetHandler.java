package org.duraspace.duradav.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import org.duraspace.duradav.core.Collection;
import org.duraspace.duradav.core.Content;
import org.duraspace.duradav.core.Path;
import org.duraspace.duradav.core.Resource;
import org.duraspace.duradav.core.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

/**
 * Handles GET requests.
 */
class GetHandler implements MethodHandler {

    static final String COLLECTION_CONTENT_TYPE = "text/html";

    public void handleRequest(WebdavStore store,
                              Resource resource,
                              HttpServletRequest req,
                              HttpServletResponse resp) throws WebdavException {
        if (resource.isCollection()) {
            Collection collection = (Collection) resource;
            HeadHandler.setCollectionHeaders(collection, resp);
            sendCollectionBody(collection, req, resp);
        } else {
            Content content = (Content) resource;
            HeadHandler.setContentHeaders(content, resp);
            sendContentBody(content, resp);
        }
    }

    private static void sendCollectionBody(Collection collection,
                                           HttpServletRequest req,
                                           HttpServletResponse resp) {
        PrintWriter writer = null;
        try {
            String title = "Directory of " + collection.getPath();
            writer = resp.getWriter();
            writer.println("<html>");
            writer.println("<head>");
            String url = req.getRequestURL().toString();
            if (!url.endsWith("/")) {
                writer.println("<base href='" + url + "/'/>");
            }
            writer.println("<title>" + title + "</title>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<h2>" + title + "</h2>");
            writer.println("<hr size='1'/>");
            writer.println("<pre>");
            if (!collection.getPath().equals(Path.ROOT)) {
                writer.println("  <a href='..'>[Parent Directory]</a>");
            }
            for (String child : collection.getChildren()) {
                writer.println("  <a href='" + child + "'>" + child + "</a>");
            }
            writer.println("    </pre>");
            writer.println("  </body>");
            writer.println("</html>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    private static void sendContentBody(Content content,
                                        HttpServletResponse resp) {
        InputStream source = null;
        OutputStream sink = null;
        try {
            source = content.getBody().getStream();
            sink = resp.getOutputStream();
            IOUtils.copy(source, sink);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(source);
            IOUtils.closeQuietly(sink);
        }
    }

}
