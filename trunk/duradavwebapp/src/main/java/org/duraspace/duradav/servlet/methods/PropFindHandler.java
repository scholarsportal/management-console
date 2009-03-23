package org.duraspace.duradav.servlet.methods;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.duraspace.duradav.error.NotFoundException;
import org.duraspace.duradav.error.WebdavException;
import org.duraspace.duradav.store.WebdavStore;

import static org.duraspace.duradav.servlet.methods.Constants.DAV_PREFIX;
import static org.duraspace.duradav.servlet.methods.Constants.DAV_URI;
import static org.duraspace.duradav.servlet.methods.Constants.UTF8;
import static org.duraspace.duradav.servlet.methods.Constants.XML_MEDIATYPE;

/**
 * Handles PROPFIND requests.
 */
class PropFindHandler implements MethodHandler {

    private static final String HTTP_200_OK = "HTTP/1.1 200 OK";

    private static final int MULTISTATUS_CODE = 207;

    private static final Logger logger = LoggerFactory.getLogger(PropFindHandler.class);

    public void handleRequest(WebdavStore store,
                              Resource resource,
                              HttpServletRequest req,
                              HttpServletResponse resp)
            throws WebdavException {
        PropFindRequest propReq = PropFindRequest.createRequest(
                resource.getPath(), req);
        resp.setStatus(MULTISTATUS_CODE);
        resp.setContentType(XML_MEDIATYPE);
        String href = req.getContextPath() + resource.getPath().toString();
        if (propReq.getType() == PropFindRequest.LIST_NAMES) {
            sendBody(store, href, resource, null, true, propReq.getDepth(), resp);
        } else {
            sendBody(store, href, resource, propReq.getNames(), false,
                     propReq.getDepth(), resp);
        }
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
            XMLStreamWriter writer = factory.createXMLStreamWriter(bout, UTF8);
            writer.writeStartDocument();
            writer.setPrefix(DAV_PREFIX, DAV_URI);
            writeStartElement(DAVElement.MULTISTATUS, writer);
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
                logger.debug("PROPFIND reponse body: " + bout.toString(UTF8));
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

    private static void writeEmptyElement(QName qName,
                                          XMLStreamWriter writer)
            throws XMLStreamException {
        writer.writeEmptyElement(qName.getNamespaceURI(),
                                 qName.getLocalPart());
    }

    private static void writeStartElement(QName qName,
                                          XMLStreamWriter writer)
            throws XMLStreamException {
        writer.writeStartElement(qName.getNamespaceURI(),
                                 qName.getLocalPart());
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
        writeStartElement(DAVElement.RESPONSE, writer);
        writeStartElement(DAVElement.HREF, writer);
        writer.writeCharacters(href);
        writer.writeEndElement();
        writeStartElement(DAVElement.PROPSTAT, writer);
        writeStartElement(DAVElement.PROP, writer);
        List<QName> notFound = new ArrayList<QName>();
        if (propNames == null) {
            // do all that exist
            if (!resource.isCollection()) {
                Content content = (Content) resource;
                writeProperty(DAVElement.GET_CONTENT_LENGTH,
                              content.getLength(),
                              omitValues,
                              writer);
                writeProperty(DAVElement.GET_CONTENT_TYPE,
                              content.getMediaType(),
                              omitValues,
                              writer);
            }
            writeProperty(DAVElement.GET_LAST_MODIFIED,
                          resource.getModifiedDate(),
                          omitValues,
                          writer);
            writeResourceType(resource.isCollection(),
                              omitValues,
                              writer);
        } else {
            // do just the requested ones that exist
            for (QName name : propNames) {
                if (name.equals(DAVElement.GET_CONTENT_LENGTH)) {
                    if (resource.isCollection()) {
                        notFound.add(name);
                    } else {
                        Content content = (Content) resource;
                        if (!writeProperty(name,
                                           content.getLength(),
                                           omitValues,
                                           writer)) {
                            notFound.add(name);
                        }
                    }
                } else if (name.equals(DAVElement.GET_CONTENT_TYPE)) {
                    if (resource.isCollection()) {
                        writeProperty(name,
                                      GetHandler.COLLECTION_CONTENT_TYPE,
                                      omitValues,
                                      writer);
                    } else {
                        Content content = (Content) resource;
                        if (!writeProperty(name,
                                           content.getMediaType(),
                                           omitValues,
                                           writer)) {
                            notFound.add(name);
                        }
                    }
                } else if (name.equals(DAVElement.GET_LAST_MODIFIED)) {
                    if (!writeProperty(name,
                                       resource.getModifiedDate(),
                                       omitValues,
                                       writer)) {
                        notFound.add(name);
                    }
                } else if (name.equals(DAVElement.RESOURCE_TYPE)) {
                    writeResourceType(resource.isCollection(),
                                      omitValues,
                                      writer);
                } else {
                    // TODO: support additional properties?
                    notFound.add(name);
                }
            }
        }
        writer.writeEndElement();
        writeStartElement(DAVElement.STATUS, writer);
        writer.writeCharacters(HTTP_200_OK);
        writer.writeEndElement();
        writer.writeEndElement();
        if (notFound.size() > 0) {
            write404PropStat(notFound, writer);
        }
        writer.writeEndElement();
    }

    private static void write404PropStat(List<QName> notFound,
                                         XMLStreamWriter writer)
            throws XMLStreamException {
        writeStartElement(DAVElement.PROPSTAT, writer);
        writeStartElement(DAVElement.PROP, writer);
        for (QName name : notFound) {
            writeEmptyElement(name, writer);
        }
        writer.writeEndElement();
        writeStartElement(DAVElement.STATUS, writer);
        writer.writeCharacters(NotFoundException.STATUS_LINE);
        writer.writeEndElement();
        writeStartElement(DAVElement.RESPONSE_DESCRIPTION, writer);
        if (notFound.size() == 1) {
            writer.writeCharacters("Requested property not found");
        } else {
            writer.writeCharacters(notFound.size()
                                   + " requested properties not found");
        }
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private static boolean writeProperty(QName name,
                                         String value,
                                         boolean omitValues,
                                         XMLStreamWriter writer)
            throws XMLStreamException {
        if (value != null) {
            if (omitValues) {
                writeEmptyElement(name, writer);
            } else {
                writeStartElement(name, writer);
                writer.writeCharacters(value);
                writer.writeEndElement();
            }
            return true;
        }
        return false;
    }

    private static boolean writeProperty(QName name,
                                         long value,
                                         boolean omitValues,
                                         XMLStreamWriter writer)
            throws XMLStreamException {
        if (value > -1) {
            writeProperty(name, "" + value, omitValues, writer);
            return true;
        }
        return false;
    }

    private static boolean writeProperty(QName name,
                                         Date value,
                                         boolean omitValues,
                                         XMLStreamWriter writer)
            throws XMLStreamException {
        if (value != null) {
            writeProperty(name, Helper.formatDate(value), omitValues, writer);
            return true;
        }
        return false;
    }

    private static void writeResourceType(boolean isCollection,
                                          boolean omitValues,
                                          XMLStreamWriter writer)
            throws XMLStreamException {
        if (omitValues || !isCollection) {
            writeEmptyElement(DAVElement.RESOURCE_TYPE, writer);
        } else {
            writeStartElement(DAVElement.RESOURCE_TYPE, writer);
            writeEmptyElement(DAVElement.COLLECTION, writer);
            writer.writeEndElement();
        }
    }

}
