package org.duraspace.duradav.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import org.duraspace.duradav.core.Collection;
import org.duraspace.duradav.core.CollectionPath;
import org.duraspace.duradav.core.Content;
import org.duraspace.duradav.core.ContentPath;
import org.duraspace.duradav.core.NotFoundException;
import org.duraspace.duradav.core.Path;
import org.duraspace.duradav.core.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

/**
 * Handles GET requests.
 */
public class GetHandler
        implements Handler {

    private final WebdavStore store;

    static final String COLLECTION_CONTENT_TYPE = "text/html";

    public GetHandler(WebdavStore store) {
        this.store = store;
    }

    /**
     * {@inheritDoc}
     */
    public void handleCollectionRequest(CollectionPath path,
                                        HttpServletRequest req,
                                        HttpServletResponse resp)
            throws WebdavException {
        Collection collection = store.getCollection(path);
        HeadHandler.setCollectionHeaders(collection, resp);
        sendCollectionBody(collection, req, resp);
    }

    /**
     * {@inheritDoc}
     */
    public void handleContentRequest(ContentPath path,
                                     HttpServletRequest req,
                                     HttpServletResponse resp)
            throws WebdavException {
        Content content = null;
        try {
            content = store.getContent(path);
        } catch (NotFoundException e) {
            // ok...but is there a collection at path + '/'?
            CollectionPath collPath = new CollectionPath(path.toString() + "/");
            if (store.hasCollection(collPath)) {
                // yes; give them that
                resp.setHeader("Content-Location", req.getRequestURL() + "/");
                handleCollectionRequest(collPath, req, resp);
            } else {
                throw e;
            }
        }
        HeadHandler.setContentHeaders(content, resp);
        sendContentBody(content, resp);
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
