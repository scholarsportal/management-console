package org.duraspace.duradav.handler;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.duraspace.duradav.core.Body;
import org.duraspace.duradav.core.Content;
import org.duraspace.duradav.core.Path;
import org.duraspace.duradav.core.WebdavException;

/**
 * Initial impl just writes to local filesystem.
 */
public class DuraspaceWebdavHandler
        implements WebdavHandler {

    private static Logger logger = LoggerFactory.getLogger(DuraspaceWebdavHandler.class);

    private final File baseDir;

    public DuraspaceWebdavHandler(File baseDir) {
        this.baseDir = baseDir;
        if (!baseDir.exists()) {
            if (!baseDir.mkdirs()) {
                throw new RuntimeException("Error creating dir " + baseDir);
            }
        }
    }

    public void handleGet(HttpServletRequest req, HttpServletResponse resp)
            throws WebdavException {
        handleGetOrHead(getPath(req), req, resp, true);
    }

    private void handleGetOrHead(Path path,
                                 HttpServletRequest req,
                                 HttpServletResponse resp,
                                 boolean isGet)
           throws WebdavException {
        if (path.denotesDir()) {
            List<String> entries = listEntries(path);
            resp.setContentType("text/html");
            resp.setStatus(HttpServletResponse.SC_OK);
            if (isGet) {
                sendEntries(entries, req, resp);
            }
        } else {
            try {
                Content content = getContent(path);
                sendContent(content, resp, isGet);
            } catch (WebdavException e) {
                if (e.getStatusCode() == HttpServletResponse.SC_NOT_FOUND) {
                    if (dirExists(path)) {
                        // they specified a dir without a /
                        String loc = req.getRequestURL() + "/";
                        resp.setHeader("Content-Location", loc);
                        handleGetOrHead(Path.fromString(path.toString() + "/"),
                                        req,
                                        resp,
                                        isGet);
                    } else {
                        throw e;
                    }
                } else {
                    throw e;
                }
            }
        }
    }

    private void sendContent(Content content,
                             HttpServletResponse resp,
                             boolean isGet) {
        if (content.getLength() > -1) {
            resp.setHeader("Content-Length", "" + content.getLength());
        }
        if (content.getMediaType() != null) {
            resp.setContentType(content.getMediaType());
        }
        if (content.getModifiedDate() != null) {
            resp.setDateHeader("Last-Modified", content.getModifiedDate().getTime());
        }
        if (isGet) {
            InputStream source = null;
            OutputStream sink = null;
            try {
                source = content.getBody().getStream();
                sink = resp.getOutputStream();
                sendData(content.getBody().getStream(), resp.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                close(source);
                close(sink);
            }
        }
    }

    private void sendEntries(List<String> entries, HttpServletRequest req, HttpServletResponse resp) {
        PrintWriter writer = null;
        try {
            writer = resp.getWriter();
            writer.println("<html><head>");
            if (!req.getRequestURL().toString().endsWith("/")) {
                writer.println("<base href='" + req.getRequestURL() + "/'/>");
            }
            writer.println("<title>Directory Listing</title>");
            writer.println("</head><body><h2>Directory Listing</h2><hr size=1>\n");
            writer.println("<a href='..'>[Parent Directory]</a><br/>");
            for (String entry : entries) {
                writer.println("<a href='" + entry + "'>" + entry + "</a><br/>");
            }
            writer.println("</body></html>");
        } catch (IOException e) {
            throw new RuntimeException("Error sending paths", e);
        } finally {
            close(writer);
        }
    }

    public void handleHead(HttpServletRequest req, HttpServletResponse resp)
            throws WebdavException {
        throw new RuntimeException("Not implemented");
    }

    public void handleMkCol(HttpServletRequest req, HttpServletResponse resp)
            throws WebdavException {
        throw new RuntimeException("Not implemented");
    }

    public void handleMove(HttpServletRequest req, HttpServletResponse resp)
            throws WebdavException {
        throw new RuntimeException("Not implemented");
    }

    public void handleOptions(HttpServletRequest req, HttpServletResponse resp)
            throws WebdavException {
        resp.setHeader("DAV", "1");
        // TODO: finish impl
    }

    public void handlePropFind(HttpServletRequest req, HttpServletResponse resp)
            throws WebdavException {
        throw new RuntimeException("Not implemented");
    }

    public void handlePropPatch(HttpServletRequest req, HttpServletResponse resp)
            throws WebdavException {
        throw new RuntimeException("Not implemented");
    }

    public void handlePut(HttpServletRequest req, HttpServletResponse resp)
            throws WebdavException {
        Path path = getPath(req);
        if (path.denotesDir()) {
            throw new WebdavException(
                    "Cannot PUT directory: " + path.toString() + " (use MKCOL)",
                    HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
        if (!dirExists(path.getParent())) {
            throw new WebdavException(
                    "Cannot PUT file: " + path.toString() + " (parent"
                    + " does not exist or is not a directory)",
                    HttpServletResponse.SC_CONFLICT);
        }

        boolean replaced;
        try {
            replaced = putFile(req.getInputStream(), path);
        } catch (IOException e) {
            throw new RuntimeException("Error writing content", e);
        }

        if (replaced) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            // TODO: According to webdav-servlet, the "Transmit" WebDAV client
            //        dies on this; needs a SC_NO_CONTENT status instead?!
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }
    }

    private static Path getPath(HttpServletRequest req) {
        if (req.getPathInfo() != null) {
            return Path.fromString(req.getPathInfo());
        } else {
            // tomcat
            return Path.fromString(req.getServletPath());
        }
    }

    // Maybe move below to store interface?

    // throws NotFoundException
    private Content getContent(Path path) throws WebdavException {
        File file = getFile(path);
        if (!file.isFile()) {
            throw new WebdavException("No such file: " + path.toString(),
                                      HttpServletResponse.SC_NOT_FOUND);
        }
        return new Content(Body.fromFile(file),
                           file.length(),
                           null,
                           new Date(file.lastModified()));
    }

    // throws NotFoundException
    private List<String> listEntries(Path parentPath) throws WebdavException {
        if (!parentPath.denotesDir()) {
            throw new IllegalArgumentException("Not a directory: " + parentPath.toString());
        }
        File dir = getFile(parentPath);
        if (!dir.isDirectory()) {
            throw new WebdavException("No such directory: " + parentPath.toString(),
                                      HttpServletResponse.SC_NOT_FOUND);
        }
        List<String> list = new ArrayList<String>();
        for (String name : dir.list()) {
            File file = new File(dir, name);
            if (file.isFile()) {
                list.add(name);
            } else {
                list.add(name + "/");
            }
        }
        return list;
    }

    private boolean dirExists(Path path) {
        return getFile(path).isDirectory();
    }

    private boolean fileExists(Path path) {
        return getFile(path).isFile();
    }

    private boolean putFile(InputStream source, Path path) {
        File file = getFile(path);
        boolean replaced = false;
        if (file.exists()) {
            replaced = true;
        }
        FileOutputStream sink = null;
        try {
            sink = new FileOutputStream(file);
            sendData(source, sink);
            return replaced;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(source);
            close(sink);
        }
    }

    // not part of interface..impl details for interface impl

    private File getFile(Path path) {
        if (path == Path.ROOT) {
            return baseDir;
        }
        String abs = path.toString();
        int minus = 0;
        if (path.denotesDir()) {
            minus = 1;
        }
        String rel = abs.substring(1, abs.length() - minus);
        return new File(baseDir, rel);
    }

    private static void sendData(InputStream source, OutputStream sink)
            throws IOException {
        byte[] buffer = new byte[4096];
        int len;
        while ((len = source.read(buffer)) > 0) {
            sink.write(buffer, 0, len);
        }
    }

    private static void close(Closeable closeable) {
        try {
            if (closeable != null) closeable.close();
        } catch (IOException e) {
            logger.warn("Error closing stream", e);
        }
    }
}
