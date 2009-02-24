package org.duraspace.storage;

import java.io.InputStream;

import java.util.List;
import java.util.Properties;

/**
 * A Storage Provider provides services which allow content to be
 * stored in and retrieved from spaces.
 *
 * @author Bill Branan
 */
public interface StorageProvider {

    public enum AccessType {OPEN, CLOSED};

    /* Names for metadata properties */
    public static final String METADATA_SPACE_NAME = "name";
    public static final String METADATA_SPACE_ACCESS = "access";
    public static final String METADATA_CONTENT_NAME = "name";

    /**
     * Provides a listing of all spaces owned by a customer.
     *
     * @return List of spaceIds
     * @throws StorageException
     */
    public List<String> getSpaces()
    throws StorageException;

    /**
     * Provides a listing of all of the content files within a space.
     *
     * @return List of contentIds
     * @throws StorageException
     */
    public List<String> getSpaceContents(String spaceId)
    throws StorageException;

    /**
     * Creates a new space.
     *
     * Depending on the storage implementation, the spaceId may be
     * changed somewhat to comply with the naming rules of the
     * underlying storage provider. The same spaceId value used
     * here can be used in all other methods, as the conversion
     * will be applied internally, however a call to getSpaces()
     * may not include a space with exactly this same name.
     *
     * @param spaceId
     * @throws StorageException
     */
    public void createSpace(String spaceId)
    throws StorageException;

    /**
     * Deletes a space.
     *
     * @param spaceId
     * @throws StorageException
     */
    public void deleteSpace(String spaceId)
    throws StorageException;

    /**
     * Retrieves the metadata associated with a space.
     *
     * @param spaceId
     * @return Properties list of space metadata or null if no metadata exists
     * @throws StorageException
     */
    public Properties getSpaceMetadata(String spaceId)
    throws StorageException;

    /**
     * Sets the metadata associated with a space.
     *
     * @param spaceId
     * @param spaceMetadata
     * @throws StorageException
     */
    public void setSpaceMetadata(String spaceId,
                                 Properties spaceMetadata)
    throws StorageException;

    /**
     * Gets the access setting of the space, either OPEN or CLOSED. An OPEN space is
     * available for public viewing. A CLOSED space requires authentication prior to
     * viewing any of the contents.
     *
     * @param spaceId
     * @return
     * @throws StorageException
     */
    public AccessType getSpaceAccess(String spaceId)
    throws StorageException;

    /**
     * Sets the accessibility of a space to either OPEN or CLOSED.
     *
     * @param spaceId
     * @param access
     * @throws StorageException
     */
    public void setSpaceAccess(String spaceId,
                               AccessType access)
    throws StorageException;

    /**
     * Adds content to a space.
     *
     * @param spaceId
     * @param contentId
     * @param content
     * @throws StorageException
     */
    public void addContent(String spaceId,
                           String contentId,
                           String contentMimeType,
                           int contentSize,
                           InputStream content)
    throws StorageException;

    /**
     * Gets content from a space.
     *
     * @param spaceId
     * @param contentId
     * @return the content stream or null if the content does not exist
     * @throws StorageException
     */
    public InputStream getContent(String spaceId,
                                  String contentId)
    throws StorageException;

    /**
     * Removes content from a space.
     *
     * @param spaceId
     * @param contentId
     * @throws StorageException
     */
    public void deleteContent(String spaceId,
                              String contentId)
    throws StorageException;

    /**
     * Sets the metadata associated with content.
     *
     * @param spaceId
     * @param contentId
     * @param contentMetadata
     * @throws StorageException
     */
    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Properties contentMetadata)
    throws StorageException;

    /**
     * Retrieves the metadata associated with content
     *
     * @param spaceId
     * @param contentId
     * @return
     * @throws StorageException
     */
    public Properties getContentMetadata(String spaceId,
                                         String contentId)
    throws StorageException;

}
