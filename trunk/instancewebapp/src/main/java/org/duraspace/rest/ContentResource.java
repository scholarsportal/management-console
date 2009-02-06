package org.duraspace.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Provides interaction with content
 *
 * @author Bill Branan
 */
public class ContentResource {

    /**
     * Retrieves content from a space.
     *
     * @param customerID
     * @param spaceID
     * @param contentID
     * @return InputStream which can be used to read content.
     */
    public static InputStream getContent(String customerID,
                                         String spaceID,
                                         String contentID) {
        String content = "content";
        ByteArrayInputStream is = new ByteArrayInputStream(content.getBytes());
        return is;
    }

    /**
     * Retrieves the properties of a piece of content.
     *
     * @param customerID
     * @param spaceID
     * @param contentID
     * @return XML listing of content properties
     */
    public static String getContentProperties(String customerID,
                                              String spaceID,
                                              String contentID) {
        String xml = "<content />";
        return xml;
    }

    /**
     * Updates the properties of a piece of content.
     *
     * @return success
     */
    public static boolean updateContentProperties(String customerID,
                                                 String spaceID,
                                                 String contentID,
                                                 String contentName) {
        return true;
    }

    /**
     * Adds content to a space.
     *
     * @return success
     */
    public static boolean addContent(String customerID,
                                     String spaceID,
                                     String contentID,
                                     InputStream content,
                                     String contentMimeType,
                                     int contentSize) {
        return true;
    }

    /**
     * Removes a piece of content.
     *
     * @param customerID
     * @param spaceID
     * @param contentID
     * @return success
     */
    public static boolean deleteContent(String customerID,
                                        String spaceID,
                                        String contentID) {
        return true;
    }

}
