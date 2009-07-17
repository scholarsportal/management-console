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
     * Builds space metadata XML tree
     */
    private static Element getSpaceXML(StorageProvider storage, String spaceID)
    throws StorageException {
        Element spaceElem = new Element("space");
        spaceElem.setAttribute("id", spaceID);

        Map<String, String> metadata = storage.getSpaceMetadata(spaceID);
        if(metadata != null) {
            Iterator<String> metadataNames = metadata.keySet().iterator();
            while(metadataNames.hasNext()) {
                String metadataName = (String)metadataNames.next();
                String metadataValue = metadata.get(metadataName);
                Element metadataElem = new Element(metadataName);
                metadataElem.setText(metadataValue);
                spaceElem.addContent(metadataElem);
            }
        }
        return spaceElem;
    }

    /**
     * Gets the metadata of a space.
     *
     * @param spaceID
     * @return Map of space metadata
     */
    public static Map<String, String> getSpaceMetadata(String spaceID, String storeID)
    throws RestResourceException {
        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);
            return storage.getSpaceMetadata(spaceID);
        } catch (StorageException e) {
            String error = "Error attempting to retrieve space metadata for '" +
                           spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

    /**
     * Gets a listing of the contents of a space.
     *
     * @param spaceID
     * @return XML listing of space contents
     */
    public static String getSpaceContents(String spaceID, String storeID)
    throws RestResourceException {
        Element spaceElem = new Element("space");
        spaceElem.setAttribute("id", spaceID);

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
                    spaceElem.addContent(contentElem);
                }
            }
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
     * Adds a space.
     *
     * @param spaceID
     * @param spaceName
     * @param spaceAccess
     */
    public static void addSpace(String spaceID,
                                String spaceName,
                                String spaceAccess,
                                Map<String, String> userMetadata,
                                String storeID)
    throws RestResourceException {
        // TODO: Check user permissions

        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);
            storage.createSpace(spaceID);
            updateSpaceMetadata(spaceID,
                                spaceName,
                                spaceAccess,
                                userMetadata,
                                storeID);
        } catch (StorageException e) {
            String error = "Error attempting to add space '" +
                           spaceID + "': " + e.getMessage();
            log.error(error, e);
            throw new RestResourceException(error);
        }
    }

    /**
     * Updates the metadata of a space.
     *
     * @param spaceID
     * @param spaceName
     * @param spaceAccess
     */
    public static void updateSpaceMetadata(String spaceID,
                                           String spaceName,
                                           String spaceAccess,
                                           Map<String, String> userMetadata,
                                           String storeID)
    throws RestResourceException {
        // TODO: Check user permissions
        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);

            Map<String, String> spaceMeta = storage.getSpaceMetadata(spaceID);
            if(spaceMeta == null) {
                spaceMeta = new HashMap<String, String>();
            }
            boolean metadataUpdated = false;

            // Update space name
            if(spaceName != null && !spaceName.equals("")) {
                spaceMeta.put(StorageProvider.METADATA_SPACE_NAME, spaceName);
                metadataUpdated = true;
            }

            // Update user metadata
            if(userMetadata != null && userMetadata.size() > 0) {
                Iterator<String> userMetaNames = userMetadata.keySet().iterator();
                while(userMetaNames.hasNext()) {
                    String userMetaName = userMetaNames.next();
                    String userMetaValue = userMetadata.get(userMetaName);
                    spaceMeta.put(userMetaName, userMetaValue);
                }
                metadataUpdated = true;
            }

            // Commit updates to space metadata
            if(metadataUpdated) {
                storage.setSpaceMetadata(spaceID, spaceMeta);
            }

            // Set space access if necessary
            if(spaceAccess != null) {
                AccessType access = storage.getSpaceAccess(spaceID);
                AccessType newAccessType = null;
                if(spaceAccess.toUpperCase().equals(AccessType.CLOSED.name())) {
                    newAccessType = AccessType.CLOSED;
                } else if(spaceAccess.toUpperCase().equals(AccessType.OPEN.name())) {
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
            String error = "Error attempting to update space metadata for '" +
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
