/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradav.servlet.methods;

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

import org.duracloud.duradav.core.Path;
import org.duracloud.duradav.error.BadRequestException;

import static org.duracloud.duradav.servlet.methods.Constants.UTF8;

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
        String depthHeader = req.getHeader(Depth.HEADER);
        Depth depth = Depth.parse(depthHeader, Depth.INFINITY);
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
            String body = IOUtils.toString(bodyStream, UTF8);
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
        if (!reader.getName().equals(DAVElement.PROPFIND)) {
            throw new XMLStreamException("Expected root element: "
                                         + DAVElement.PROPFIND);
        }

        reader.nextTag();
        QName name = reader.getName();
        if (name.equals(DAVElement.ALLPROP)) {
            logger.debug("PROPFIND: Client requested all values");
            return new PropFindRequest(ALL, depth, null);
        } else if (name.equals(DAVElement.PROP)) {
            List<QName> names = new ArrayList<QName>();
            int event = reader.nextTag();
            name = reader.getName();
            while (!(name.equals(DAVElement.PROP)
                    && event == XMLEvent.END_ELEMENT)) {
                if (event == XMLEvent.START_ELEMENT) {
                    names.add(name);
                }
                event = reader.nextTag();
                name = reader.getName();
            }
            logger.debug("PROPFIND: Client requested " + names.size() + " by name");
            return new PropFindRequest(BY_NAME, depth, names);
        } else if (name.equals(DAVElement.PROPNAME)) {
            logger.debug("PROPFIND: Client requested list of names");
            return new PropFindRequest(LIST_NAMES, depth, null);
        }
        throw new XMLStreamException("Expected first child of root element to"
                + " be " + DAVElement.ALLPROP + ", " + DAVElement.PROP
                + ", or " + DAVElement.PROPNAME);
    }

}
