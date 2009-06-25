package org.duraspace.customerwebapp.rest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import org.duraspace.common.web.RestResourceException;
import org.duraspace.customerwebapp.util.StorageProviderFactory;
import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.provider.StorageProvider;
import org.duraspace.storage.provider.StorageProvider.AccessType;
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
    public static String getSpaces(String storeID)
    throws RestResourceException {
        Element spacesElem = new Element("spaces");

        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);

            Iterator<String> spaces = storage.getSpaces();
            while(spaces.hasNext()) {
                String spaceID = spaces.next();
                try {
                    Element spaceElem = getSpaceXML(storage, spaceID);
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
     * @param spaceID
     * @return XML listing of space properties
     */
    public static String getSpaceProperties(String spaceID, String storeID)
    throws RestResourceException {
        Element spaceElem;

        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);
            spaceElem = getSpaceXML(storage, spaceID);
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
    private static Element getSpaceXML(StorageProvider storage, String spaceID)
    throws StorageException {
        Element spaceElem = new Element("space");
        spaceElem.setAttribute("id", spaceID);
        Element propsElem = new Element("properties");
        spaceElem.addContent(propsElem);

        Map<String, String> metadata = storage.getSpaceMetadata(spaceID);
        if(metadata != null) {
            Iterator<String> metadataNames = metadata.keySet().iterator();
            while(metadataNames.hasNext()) {
                String metadataName = (String)metadataNames.next();
                String metadataValue = metadata.get(metadataName);
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
     * @param spaceID
     * @return XML listing of space contents
     */
    public static String getSpaceContents(String spaceID, String storeID)
    throws RestResourceException {
        Element root = new Element("space");
        root.setAttribute("id", spaceID);
        Element content = new Element("content");
        root.addContent(content);

        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);

            AccessType access = storage.getSpaceAccess(spaceID);
            if(access.equals(AccessType.CLOSED)) {
                // TODO: Check user permissions
            }

            Iterator<String> contents = storage.getSpaceContents(spaceID);
            if(contents != null) {
                while(contents.hasNext()) {
                    String contentItem = contents.next();
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
     * @param spaceID
     * @param spaceName
     * @param spaceAccess
     */
    public static void addSpace(String spaceID,
                                String spaceName,
                                String spaceAccess,
                                String storeID)
    throws RestResourceException {
        // TODO: Check user permissions

        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);
            storage.createSpace(spaceID);
            updateSpaceProperties(spaceID, spaceName, spaceAccess, storeID);
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
     * @param spaceID
     * @param spaceName
     * @param spaceAccess
     */
    public static void updateSpaceProperties(String spaceID,
                                             String spaceName,
                                             String spaceAccess,
                                             String storeID)
    throws RestResourceException {
        // TODO: Check user permissions

        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);

            // Set space properties if a new value was provided
            if(spaceName != null && !spaceName.equals("")) {
                Map<String, String> spaceProps = storage.getSpaceMetadata(spaceID);
                if(spaceProps == null) {
                    spaceProps = new HashMap<String, String>();
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
     * @param spaceID
     */
    public static void deleteSpace(String spaceID, String storeID)
    throws RestResourceException {
        // TODO: Check user permissions

        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);
            storage.deleteSpace(spaceID);
        } catch (StorageException e) {
            String error = "Error attempting to delete space '" +
                           spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

}
