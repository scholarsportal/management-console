package org.duraspace.duradav.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.duraspace.duradav.core.BadRequestException;
import org.duraspace.duradav.core.Path;

class PropFindRequest {

    public static final int ALL = 0;

    public static final int BY_NAME = 1;

    public static final int LIST_NAMES = 2;

    private static final Logger logger = LoggerFactory.getLogger(PropFindRequest.class);

    private final int type;

    private final Depth depth;

    private final Iterable<QName> names;

    PropFindRequest(int type, Depth depth, Iterable<QName> names) {
        this.depth = depth;
        this.type = type;
        this.names = names;
    }

    public Depth getDepth() {
        return depth;
    }

    public int getType() {
        return type;
    }

    public Iterable<QName> getNames() {
        return names;
    }

    public static PropFindRequest createRequest(Path path,
                                                HttpServletRequest req)
            throws BadRequestException {
        String depthHeader = req.getHeader("Depth");
        Depth depth = Depth.parse(req.getHeader("Depth"), Depth.INFINITY);
        if (depth == null) {
            throw new BadRequestException(path, "Invalid Depth header: "
                                          + depthHeader);
        }

        int len = req.getContentLength();
        if (len == 0) {
            return new PropFindRequest(PropFindRequest.ALL, depth, null);
        }

        InputStream bodyStream = null;
        try {
            bodyStream = req.getInputStream();
            String body = IOUtils.toString(bodyStream, "UTF-8");
            if ("".equals(body)) {
                return new PropFindRequest(PropFindRequest.ALL, depth, null);
            }
            logger.debug("PROPFIND request body: {}", body);

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(
                    new StringReader(body));
            try {
                return createRequest(path, depth, reader);
            } finally {
                reader.close();
            }
        } catch (XMLStreamException e) {
            throw new BadRequestException(path,
                                          "Invalid PROPFIND request body: "
                                          + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(bodyStream);
        }
    }

    private static PropFindRequest createRequest(Path path,
                                                 Depth depth,
                                                 XMLStreamReader reader)
            throws XMLStreamException {

        reader.nextTag();
        if (!reader.getNamespaceURI().equals("DAV:")
                || !reader.getLocalName().equals("propfind")) {
            throw new XMLStreamException("Expected DAV:propfind root element");
        }

        reader.nextTag();
        if (reader.getNamespaceURI().equals("DAV:")) {
            if (reader.getLocalName().equals("allprop")) {
                return new PropFindRequest(ALL, depth, null);
            } else if (reader.getLocalName().equals("prop")) {
                List<QName> names = new ArrayList<QName>();
                QName prop = new QName("DAV:", "prop");
                int event = reader.nextTag();
                while (!(reader.getName().equals(prop)
                        && event == XMLEvent.END_ELEMENT)) {
                    if (event == XMLEvent.START_ELEMENT) {
                        names.add(reader.getName());
                    }
                    event = reader.nextTag();
                }
                return new PropFindRequest(BY_NAME, depth, names);
            } else if (reader.getLocalName().equals("propname")) {
                return new PropFindRequest(LIST_NAMES, depth, null);
            }
        }
        throw new XMLStreamException("Expected DAV:allprop, DAV:propname,"
                + " or DAV:prop as first child of DAV:propfind root element");
    }

}
