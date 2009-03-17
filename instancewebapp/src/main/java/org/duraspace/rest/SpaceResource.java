package org.duraspace.rest;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import org.duraspace.common.web.RestResourceException;
import org.duraspace.storage.StorageException;
import org.duraspace.storage.StorageProvider;
import org.duraspace.storage.StorageProvider.AccessType;
import org.duraspace.util.StorageProviderUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * Provides interaction with spaces
 *
 * @author Bill Branan
 */
public class SpaceResource {

    protected static final Logger log = Logger.getLogger(SpaceResource.class);

    /**
     * Provides a listing of all spaces for a customer. Open spaces are
     * always included in the list, closed spaces are included based
     * on user authorization.
     *
     * @return XML listing of spaces
     */
    public static String getSpaces(String accountID)
    throws RestResourceException {
        Element spacesElem = new Element("spaces");

        try {
            StorageProvider storage =
                StorageProviderUtil.getStorageProvider(accountID);

            List<String> spaces = storage.getSpaces();
            for(String spaceID : spaces) {
                try {
                    Element spaceElem = getSpaceXML(accountID, spaceID);
                    spacesElem.addContent(spaceElem);
                } catch(StorageException e) {
                    // This bucket may not be part of DuraSpace, log the
                    // error and continue attempting to build spaces list
                    String error = "Error attempting to build space XML for '" +
                                   spaceID + "': " + e.getMessage();
                    log.warn(error);
                }
            }
        } catch (StorageException e) {
            String error = "Error attempting to build spaces XML: " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }

        Document doc = new Document(spacesElem);
        XMLOutputter xmlConverter = new XMLOutputter();
        return xmlConverter.outputString(doc);
    }

    /**
     * Gets the properties of a space.
     *
     * @param accountID
     * @param spaceID
     * @return XML listing of space properties
     */
    public static String getSpaceProperties(String accountID,
                                            String spaceID)
    throws RestResourceException {
        Element spaceElem;

        try {
            spaceElem = getSpaceXML(accountID, spaceID);
        } catch (StorageException e) {
            String error = "Error attempting to build space XML for '" +
                           spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }

        Document doc = new Document(spaceElem);
        XMLOutputter xmlConverter = new XMLOutputter();
        return xmlConverter.outputString(doc);
    }

    /**
     * Builds space properties XML tree
     */
    private static Element getSpaceXML(String accountID,
                                       String spaceID)
    throws StorageException {
        Element spaceElem = new Element("space");
        spaceElem.setAttribute("id", spaceID);
        Element propsElem = new Element("properties");
        spaceElem.addContent(propsElem);

        StorageProvider storage =
            StorageProviderUtil.getStorageProvider(accountID);

        Properties metadata = storage.getSpaceMetadata(spaceID);
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
        return spaceElem;
    }

    /**
     * Gets a listing of the contents of a space.
     *
     * @param accountID
     * @param spaceID
     * @return XML listing of space contents
     */
    public static String getSpaceContents(String accountID,
                                          String spaceID)
    throws RestResourceException {
        Element root = new Element("space");
        root.setAttribute("id", spaceID);
        Element content = new Element("content");
        root.addContent(content);

        try {
            StorageProvider storage =
                StorageProviderUtil.getStorageProvider(accountID);

            AccessType access = storage.getSpaceAccess(spaceID);
            if(access.equals(AccessType.CLOSED)) {
                // TODO: Check user permissions
            }

            List<String> contents = storage.getSpaceContents(spaceID);
            if(contents != null) {
                for(String contentItem : contents) {
                    Element contentElem = new Element("item");
                    contentElem.setText(contentItem);
                    content.addContent(contentElem);
                }
            }
        } catch (StorageException e) {
            String error = "Error attempting to build space XML for '" +
                           spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }

        Document doc = new Document(root);
        XMLOutputter xmlConverter = new XMLOutputter();
        return xmlConverter.outputString(doc);
    }

    /**
     * Adds a space.
     *
     * @param accountID
     * @param spaceID
     * @param spaceName
     * @param spaceAccess
     */
    public static void addSpace(String accountID,
                                String spaceID,
                                String spaceName,
                                String spaceAccess)
    throws RestResourceException {
        // TODO: Check user permissions

        try {
            StorageProvider storage =
                StorageProviderUtil.getStorageProvider(accountID);
            storage.createSpace(spaceID);
            updateSpaceProperties(accountID, spaceID, spaceName, spaceAccess);
        } catch (StorageException e) {
            String error = "Error attempting to add space '" +
                           spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

    /**
     * Updates the properties of a space.
     *
     * @param accountID
     * @param spaceID
     * @param spaceName
     * @param spaceAccess
     */
    public static void updateSpaceProperties(String accountID,
                                             String spaceID,
                                             String spaceName,
                                             String spaceAccess)
    throws RestResourceException {
        // TODO: Check user permissions

        try {
            StorageProvider storage =
                StorageProviderUtil.getStorageProvider(accountID);

            // Set space properties if a new value was provided
            if(spaceName != null && !spaceName.equals("")) {
                Properties spaceProps = storage.getSpaceMetadata(spaceID);
                if(spaceProps == null) {
                    spaceProps = new Properties();
                }
                spaceProps.put(StorageProvider.METADATA_SPACE_NAME, spaceName);
                storage.setSpaceMetadata(spaceID, spaceProps);
            }

            // Set space access if a new value was provided
            if(spaceAccess != null) {
                AccessType access = storage.getSpaceAccess(spaceID);
                AccessType newAccessType = null;
                if(spaceAccess.toUpperCase().equals("CLOSED")) {
                    newAccessType = AccessType.CLOSED;
                } else if(spaceAccess.toUpperCase().equals("OPEN")) {
                    newAccessType = AccessType.OPEN;
                }

                if(null == newAccessType) {
                    String error = "Space Access must be set to either OPEN or CLOSED. '" +
                                    spaceAccess +"' is not a valid access setting";
                    throw new RestResourceException(error);
                } else {
                    if(!access.equals(newAccessType)) {
                        storage.setSpaceAccess(spaceID, newAccessType);
                    }
                }
            }
        } catch (StorageException e) {
            String error = "Error attempting to update space properties for '" +
                           spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

    /**
     * Deletes a space, removing all included content.
     *
     * @param accountID
     * @param spaceID
     */
    public static void deleteSpace(String accountID,
                                      String spaceID)
    throws RestResourceException {
        // TODO: Check user permissions

        try {
            StorageProvider storage =
                StorageProviderUtil.getStorageProvider(accountID);
            storage.deleteSpace(spaceID);
        } catch (StorageException e) {
            String error = "Error attempting to delete space '" +
                           spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

}
