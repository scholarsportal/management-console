package org.duraspace.rest;

import java.io.InputStream;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

import org.duraspace.common.web.RestResourceException;
import org.duraspace.storage.StorageException;
import org.duraspace.storage.StorageProvider;
import org.duraspace.storage.StorageProviderUtility;
import org.duraspace.storage.StorageProvider.AccessType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * Provides interaction with content
 *
 * @author Bill Branan
 */
public class ContentResource {

    private static final Logger log = Logger.getLogger(ContentResource.class);

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
                                         String contentID)
    throws RestResourceException {
        try {
            StorageProvider storage =
                StorageProviderUtility.getStorageProvider(customerID);

            AccessType access = storage.getSpaceAccess(spaceID);
            if(access.equals(AccessType.CLOSED)) {
                // TODO: Check user permissions
            }

            return storage.getContent(spaceID, contentID);
        } catch (StorageException e) {
            String error = "Error attempting to get content '" + contentID +
                           "' in '" + spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
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
                                              String contentID)
    throws RestResourceException {
        Element contentElem = new Element("content");
        contentElem.setAttribute("id", spaceID);
        Element propsElem = new Element("properties");
        contentElem.addContent(propsElem);

        try {
            StorageProvider storage =
                StorageProviderUtility.getStorageProvider(customerID);

            AccessType access = storage.getSpaceAccess(spaceID);
            if(access.equals(AccessType.CLOSED)) {
                // TODO: Check user permissions
            }

            Properties metadata = storage.getContentMetadata(spaceID, contentID);
            if(metadata != null) {
                Enumeration<?> metadataNames = metadata.propertyNames();
                while(metadataNames.hasMoreElements()) {
                    String metadataName = (String)metadataNames.nextElement();
                    String metadataValue = metadata.getProperty(metadataName);
                    Element metadataElem = new Element(metadataName);
                    metadataElem.setText(metadataValue);
                    propsElem.addContent(metadataElem);
                }
            }
        } catch (StorageException e) {
            String error = "Error attempting to get properties for content '" +
                           contentID + "' in '" + spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }

        Document doc = new Document(contentElem);
        XMLOutputter xmlConverter = new XMLOutputter();
        return xmlConverter.outputString(doc);
    }

    /**
     * Updates the properties of a piece of content.
     *
     * @return success
     */
    public static void updateContentProperties(String customerID,
                                               String spaceID,
                                               String contentID,
                                               String contentName,
                                               String contentMimeType)
    throws RestResourceException {
        // TODO: Check user permissions

        try {
            StorageProvider storage =
                StorageProviderUtility.getStorageProvider(customerID);

            Properties metadata = storage.getContentMetadata(spaceID, contentID);

            // Set content name if a new value was provided
            if(contentName != null && !contentName.equals("")) {
                metadata.put(StorageProvider.METADATA_CONTENT_NAME, contentName);
            }

            // Set content mime type if a new value was provided
            if(contentMimeType != null && !contentMimeType.equals("")) {
                metadata.put(StorageProvider.METADATA_CONTENT_MIMETYPE, contentMimeType);
            }

            storage.setContentMetadata(spaceID, contentID, metadata);
        } catch (StorageException e) {
            String error = "Error attempting to update properties for content '" +
                           contentID + "' in '" + spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

    /**
     * Adds content to a space.
     *
     * @return success
     */
    public static void addContent(String customerID,
                                     String spaceID,
                                     String contentID,
                                     InputStream content,
                                     String contentMimeType,
                                     int contentSize)
    throws RestResourceException {
        // TODO: Check user permissions

        try {
            StorageProvider storage =
                StorageProviderUtility.getStorageProvider(customerID);

            storage.addContent(spaceID,
                               contentID,
                               contentMimeType,
                               contentSize,
                               content);
        } catch (StorageException e) {
            String error = "Error attempting to add content '" + contentID +
                           "' in '" + spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

    /**
     * Removes a piece of content.
     *
     * @param customerID
     * @param spaceID
     * @param contentID
     * @return success
     */
    public static void deleteContent(String customerID,
                                        String spaceID,
                                        String contentID)
    throws RestResourceException {
        // TODO: Check user permissions

        try {
            StorageProvider storage =
                StorageProviderUtility.getStorageProvider(customerID);

            storage.deleteContent(spaceID, contentID);
        } catch (StorageException e) {
            String error = "Error attempting to delete content '" + contentID +
                           "' in '" + spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

}
