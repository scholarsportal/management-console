package org.duracloud.duradav.servlet.methods;

import javax.xml.namespace.QName;

import static org.duracloud.duradav.servlet.methods.Constants.DAV_PREFIX;
import static org.duracloud.duradav.servlet.methods.Constants.DAV_URI;

abstract class DAVElement {

    // resource property names
    public static final QName GET_CONTENT_LENGTH = new QName(DAV_URI, "getcontentlength", DAV_PREFIX);
    public static final QName GET_CONTENT_TYPE   = new QName(DAV_URI, "getcontenttype", DAV_PREFIX);
    public static final QName GET_LAST_MODIFIED  = new QName(DAV_URI, "getlastmodified", DAV_PREFIX);
    public static final QName RESOURCE_TYPE      = new QName(DAV_URI, "resourcetype", DAV_PREFIX);

    // other elements
    public static final QName ALLPROP              = new QName(DAV_URI, "allprop", DAV_PREFIX);
    public static final QName COLLECTION           = new QName(DAV_URI, "collection", DAV_PREFIX);
    public static final QName HREF                 = new QName(DAV_URI, "href", DAV_PREFIX);
    public static final QName MULTISTATUS          = new QName(DAV_URI, "multistatus", DAV_PREFIX);
    public static final QName PROP                 = new QName(DAV_URI, "prop", DAV_PREFIX);
    public static final QName PROPFIND             = new QName(DAV_URI, "propfind", DAV_PREFIX);
    public static final QName PROPNAME             = new QName(DAV_URI, "propname", DAV_PREFIX);
    public static final QName PROPSTAT             = new QName(DAV_URI, "propstat", DAV_PREFIX);
    public static final QName RESPONSE             = new QName(DAV_URI, "response", DAV_PREFIX);
    public static final QName RESPONSE_DESCRIPTION = new QName(DAV_URI, "responsedescription", DAV_PREFIX);
    public static final QName STATUS               = new QName(DAV_URI, "status", DAV_PREFIX);
}
