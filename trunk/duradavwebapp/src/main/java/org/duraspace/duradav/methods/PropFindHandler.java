package org.duraspace.duradav.methods;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.duraspace.duradav.core.Collection;
import org.duraspace.duradav.core.CollectionPath;
import org.duraspace.duradav.core.Content;
import org.duraspace.duradav.core.ContentPath;
import org.duraspace.duradav.core.Resource;
import org.duraspace.duradav.core.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

/**
 * Handles PROPFIND requests.
 */
class PropFindHandler implements MethodHandler {

    private static final Logger logger = LoggerFactory.getLogger(PropFindHandler.class);

    private static final String DAV_PREFIX = "D";

    private static final String DAV_URI = "DAV:";

    private static final String RFC_1123_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    public void handleRequest(WebdavStore store,
                              Resource resource,
                              HttpServletRequest req,
                              HttpServletResponse resp)
            throws WebdavException {
        PropFindRequest propReq = PropFindRequest.createRequest(
                resource.getPath(), req);
        sendMultiStatusHeader(resp);
        String href = req.getContextPath() + resource.getPath().toString();
        if (propReq.getType() == PropFindRequest.LIST_NAMES) {
            sendBody(store, href, resource, null, true, propReq.getDepth(), resp);
        } else {
            sendBody(store, href, resource, propReq.getNames(), false,
                     propReq.getDepth(), resp);
        }
    }

    private static void sendMultiStatusHeader(HttpServletResponse resp) {
        resp.setStatus(207);
        resp.setContentType("application/xml; charset=\"utf-8\"");
    }

    private static void sendBody(WebdavStore store,
                                 String href,
                                 Resource resource,
                                 Iterable<QName> propNames,
                                 boolean omitValues,
                                 Depth depth,
                                 HttpServletResponse resp) {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        OutputStream out = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            XMLStreamWriter writer = factory.createXMLStreamWriter(bout, "UTF-8");
            writer.writeStartDocument();
            writer.setPrefix(DAV_PREFIX, DAV_URI);
            writer.writeStartElement(DAV_URI, "multistatus");
            writer.writeNamespace(DAV_PREFIX, DAV_URI);
            writeResponseElement(href, resource, propNames, omitValues, writer);
            if (resource.isCollection() && depth != Depth.ZERO) {
                writeChildResponseElements(store,
                                           href,
                                           (Collection) resource,
                                           propNames,
                                           omitValues,
                                           depth == Depth.INFINITY,
                                           writer);
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.close();
            if (logger.isDebugEnabled()) {
                logger.debug("PROPFIND reponse body: " + bout.toString("UTF-8"));
            }
            byte[] body = bout.toByteArray();
            InputStream in = new ByteArrayInputStream(body);
            resp.setContentLength(body.length);
            out = resp.getOutputStream();
            IOUtils.copy(in, out);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(out);
        }

    }

    private static void writeChildResponseElements(WebdavStore store,
                                                   String href,
                                                   Collection collection,
                                                   Iterable<QName> propNames,
                                                   boolean omitValues,
                                                   boolean recursive,
                                                   XMLStreamWriter writer)
            throws XMLStreamException {
        for (String childName : collection.getChildren()) {
            String childHRef = href + childName;
            Resource child = getChild(store, collection, childName);
            writeResponseElement(childHRef, child, propNames, omitValues, writer);
            if (child.isCollection() && recursive) {
                writeChildResponseElements(store, childHRef, (Collection) child,
                        propNames, omitValues, recursive, writer);
            }
        }
    }

    private static Resource getChild(WebdavStore store,
                                     Collection collection,
                                     String childName) {
        String childPathString = collection.getPath() + childName;
        try {
            if (childName.endsWith("/")) {
                return store.getCollection(new CollectionPath(childPathString));
            } else {
                return store.getContent(new ContentPath(childPathString));
            }
        } catch (WebdavException e) {
            throw new RuntimeException("Unexpected error getting existing"
                    + " resource: " + childPathString, e);
        }
    }

    private static void writeResponseElement(String href,
                                             Resource resource,
                                             Iterable<QName> propNames,
                                             boolean omitValues,
                                             XMLStreamWriter writer)
            throws XMLStreamException {
        writer.writeStartElement(DAV_URI, "response");
        writer.writeStartElement(DAV_URI, "href");
        writer.writeCharacters(href);
        writer.writeEndElement();
        writer.writeStartElement(DAV_URI, "propstat");
        writer.writeStartElement(DAV_URI, "prop");
        if (propNames == null) {
            // do all that exist
            if (!resource.isCollection()) {
                Content content = (Content) resource;
                writeDAVProperty("getcontentlength",
                                 content.getLength(),
                                 omitValues,
                                 writer);
                writeDAVProperty("getcontenttype",
                                 content.getMediaType(),
                                 omitValues,
                                 writer);
            }
            writeDAVProperty("getlastmodified",
                             resource.getModifiedDate(),
                             omitValues,
                             writer);
            writeResourceType(resource.isCollection(),
                              omitValues,
                              writer);
        } else {
            // do just the requested ones that exist
            for (QName q : propNames) {
                if (q.getNamespaceURI().equals(DAV_URI)) {
                    String n = q.getLocalPart();
                    if (!resource.isCollection()) {
                        Content content = (Content) resource;
                        if (n.equals("getcontentlength")) {
                            writeDAVProperty(n,
                                             content.getLength(),
                                             omitValues,
                                             writer);
                        } else if (n.equals("getcontenttype")) {
                            writeDAVProperty(n,
                                             content.getMediaType(),
                                             omitValues,
                                             writer);
                        }
                    }
                    if (n.equals("getlastmodified")) {
                        writeDAVProperty(n,
                                         resource.getModifiedDate(),
                                         omitValues,
                                         writer);
                    } else if (n.equals("resourcetype")) {
                        writeResourceType(resource.isCollection(),
                                          omitValues,
                                          writer);
                    }
                }
            }
        }
        writer.writeEndElement();
        writer.writeStartElement(DAV_URI, "status");
        writer.writeCharacters("HTTP/1.1 200 OK");
        writer.writeEndElement();
        writer.writeEndElement();
        // TODO: do a 404 that includes any requested props that didn't exist
        writer.writeEndElement();
    }

    private static boolean writeDAVProperty(String name,
                                            String value,
                                            boolean omitValues,
                                            XMLStreamWriter writer)
            throws XMLStreamException {
        if (value != null) {
            if (omitValues) {
                writer.writeEmptyElement(DAV_URI, name);
            } else {
                writer.writeStartElement(DAV_URI, name);
                writer.writeCharacters(value);
                writer.writeEndElement();
            }
            return true;
        }
        return false;
    }

    private static boolean writeDAVProperty(String name,
                                            long value,
                                            boolean omitValues,
                                            XMLStreamWriter writer)
            throws XMLStreamException {
        if (value > -1) {
            writeDAVProperty(name, "" + value, omitValues, writer);
            return true;
        }
        return false;
    }

    private static boolean writeDAVProperty(String name,
                                            Date value,
                                            boolean omitValues,
                                            XMLStreamWriter writer)
            throws XMLStreamException {
        if (value != null) {
            DateFormat formatter = new SimpleDateFormat(RFC_1123_FORMAT,
                                                        Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            writeDAVProperty(name, formatter.format(value), omitValues, writer);
            return true;
        }
        return false;
    }

    private static void writeResourceType(boolean isCollection,
                                          boolean omitValues,
                                          XMLStreamWriter writer)
            throws XMLStreamException {
        if (omitValues || !isCollection) {
            writer.writeEmptyElement(DAV_URI, "resourcetype");
        } else {
            writer.writeStartElement(DAV_URI, "resourcetype");
            writer.writeEmptyElement(DAV_URI, "collection");
            writer.writeEndElement();
        }
    }

}
