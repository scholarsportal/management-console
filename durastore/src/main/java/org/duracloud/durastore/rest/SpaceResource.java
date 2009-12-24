package org.duracloud.durastore.rest;

import org.apache.log4j.Logger;
import org.duracloud.common.web.RestResourceException;
import org.duracloud.durastore.util.StorageProviderFactory;
import org.duracloud.storage.error.StorageException;
import org.duracloud.storage.provider.StorageProvider;
import org.duracloud.storage.provider.StorageProvider.AccessType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
     * @param storeID
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
                Element spaceElem = new Element("space");
                spaceElem.setAttribute("id", spaceID);
                spacesElem.addContent(spaceElem);
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
     * Gets the metadata of a space.
     *
     * @param spaceID
     * @param storeID
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
     * @param storeID
     * @param prefix
     * @param maxResults
     * @param marker
     * @return XML listing of space contents
     */
    public static String getSpaceContents(String spaceID,
                                          String storeID,
                                          String prefix,
                                          long maxResults,
                                          String marker)
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

            List<String> contents = storage.getSpaceContentsChunked(spaceID,
                                                                    prefix,
                                                                    maxResults,
                                                                    marker);
            if(contents != null) {
                for(String contentItem : contents) {
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
     * @param spaceAccess
     * @param userMetadata
     * @param storeID
     */
    public static void addSpace(String spaceID,
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
     * @param spaceAccess
     * @param userMetadata
     * @param storeID
     */
    public static void updateSpaceMetadata(String spaceID,
                                           String spaceAccess,
                                           Map<String, String> userMetadata,
                                           String storeID)
    throws RestResourceException {
        // TODO: Check user permissions
        try {
            StorageProvider storage =
                StorageProviderFactory.getStorageProvider(storeID);

            // Update space metadata
            if(userMetadata != null) {
                storage.setSpaceMetadata(spaceID, userMetadata);
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
     * @param storeID
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
