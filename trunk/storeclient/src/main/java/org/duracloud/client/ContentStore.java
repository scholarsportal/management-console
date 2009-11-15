package org.duracloud.client;

import org.duracloud.domain.Content;
import org.duracloud.domain.Space;
import org.duracloud.storage.provider.StorageProvider;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Provides access to a content store
 *
 * @author Bill Branan
 */
public interface ContentStore {

    public enum AccessType {OPEN, CLOSED};

    /** Basic space metadata: Created date */
    public static final String SPACE_CREATED =
            StorageProvider.METADATA_SPACE_CREATED;

    /** Basic space metadata: Count of items in a space */
    public static final String SPACE_COUNT =
            StorageProvider.METADATA_SPACE_COUNT;

    /** Basic space metadata: Space access (OPEN or CLOSED) */
    public static final String SPACE_ACCESS =
            StorageProvider.METADATA_SPACE_ACCESS;

    /** Basic content metadata: MIME type */
    public static final String CONTENT_MIMETYPE =
            StorageProvider.METADATA_CONTENT_MIMETYPE;

    /** Basic content metadata: Size */
    public static final String CONTENT_SIZE =
            StorageProvider.METADATA_CONTENT_SIZE;

    /** Basic content metadata: MD5 checksum */
    public static final String CONTENT_CHECKSUM =
            StorageProvider.METADATA_CONTENT_CHECKSUM;

    /** Basic content metadata: Last modified date */
    public static final String CONTENT_MODIFIED =
            StorageProvider.METADATA_CONTENT_MODIFIED;

    /**
     * Gets the base URL pointing to the DuraCloud DuraStore REST API
     */
    public String getBaseURL();
    
    /**
     * Gets the ID of this content store
     */
    public String getStoreId();

    /**
     * Gets the storage provider type
     * {@link org.duracloud.storage.domain.StorageProviderType}
     */
    public String getStorageProviderType();

    /**
     * Provides a listing of all spaces IDs.
     *
     * @return Iterator listing spaceIds
     * @throws ContentStoreException if space IDs cannot be retrieved
     */
    public List<String> getSpaces() throws ContentStoreException;

    /**
     * Provides a Space, including a listing of all of the content files within
     * a space and the metadata associated with the space.
     *
     * @param spaceId the identifier of the DuraCloud Space
     * @return Space
     * @throws ContentStoreException if the space does not exist or cannot be retrieved
     */
    public Space getSpace(String spaceId) throws ContentStoreException;

    /**
     * Provides a Space as above, but with a constrained list of content files
     * based on the parameters.
     * @param spaceId 
     * @param prefix The prefix of the content id - null for no constraints.
     * @param marker The content id marking the last item in the previous set - null to specify first set of ids
     * @param maxResults The maximum number of content ids to return in the list - null means no limit
     * @return
     * @throws ContentStoreException
     */
    public Space getSpace(String spaceId, String prefix, String marker, Integer maxResults) 
        throws ContentStoreException;
    /**
     * Creates a new space. Depending on the storage implementation, the spaceId
     * may be changed somewhat to comply with the naming rules of the underlying
     * storage provider. The same spaceId value used here can be used in all
     * other methods, as the conversion will be applied internally, however a
     * call to getSpaces() may not include a space with exactly this same name.
     *
     * @param spaceId the identifier of the DuraCloud Space
     * @param spaceMetadata a map of metadata entries for the space
     * @throws ContentStoreException if the space already exists or cannot be created
     */
    public void createSpace(String spaceId, Map<String, String> spaceMetadata)
            throws ContentStoreException;   

    /**
     * Deletes a space.
     *
     * @param spaceId the identifier of the DuraCloud Space
     * @throws ContentStoreException if the space does not exist or cannot be deleted
     */
    public void deleteSpace(String spaceId) throws ContentStoreException;
    
    /**
     * Retrieves the metadata associated with a space.
     *
     * @param spaceId the identifier of the DuraCloud Space
     * @return Map of space metadata or null if no metadata exists
     * @throws ContentStoreException if the space does not exist or the metadata cannot be retrieved
     */
    public Map<String, String> getSpaceMetadata(String spaceId)
            throws ContentStoreException;
    
    /**
     * Sets the metadata associated with a space. Only values included
     * in the  metadata map will be updated, others will remain unchanged.
     *
     * @param spaceId the identifier of the DuraCloud Space
     * @param spaceMetadata a map of metadata entries for the space
     * @throws ContentStoreException if the space does not exist or the metadata cannot be set
     */
    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata)
            throws ContentStoreException;
    
    /**
     * Gets the access setting of the space, either OPEN or CLOSED. An OPEN
     * space is available for public viewing. A CLOSED space requires
     * authentication prior to viewing any of the contents.
     *
     * @param spaceId the identifier of the DuraCloud Space
     * @return AccessType - OPEN or CLOSED
     * @throws ContentStoreException if the space does not exist or access cannot be retrieved
     */
    public AccessType getSpaceAccess(String spaceId) throws ContentStoreException;
    
    /**
     * Sets the accessibility of a space to either OPEN or CLOSED.
     *
     * @param spaceId the identifier of the DuraCloud Space
     * @param spaceAccess the access setting for this space - OPEN or CLOSED
     * @throws ContentStoreException if the space does not exist or access cannot be set
     */
    public void setSpaceAccess(String spaceId, AccessType spaceAccess)
            throws ContentStoreException;
    
    /**
     * Adds content to a space. The contentId of the new content item can
     * include "/" symbols to indicate a folder heirarchy.
     * Returns the checksum of the content as computed by the
     * underlying storage provider to facilitate comparison
     *
     * @param spaceId the identifier of the DuraCloud Space
     * @param contentId the identifier of the new content item
     * @param content the new content as a stream
     * @param contentMimeType the MIME type of the content
     * @param contentSize the size of the content
     * @param contentMetadata a map of metadata values to be linked to this content
     * @throws ContentStoreException if the space does not exist or content cannot be added
     * @return content checksum
     */
    public String addContent(String spaceId,
                             String contentId,
                             InputStream content,
                             long contentSize,
                             String contentMimeType,
                             Map<String, String> contentMetadata)
            throws ContentStoreException;

    /**
     * Gets content from a space.
     *
     * @param spaceId the identifier of the DuraCloud Space
     * @param contentId the identifier of the content item
     * @return the content stream
     * @throws ContentStoreException if the space or content does not exist or the content cannot be retrieved
     */
    public Content getContent(String spaceId, String contentId)
            throws ContentStoreException;

    /**
     * Removes content from a space.
     *
     * @param spaceId the identifier of the DuraCloud Space
     * @param contentId the identifier of the content item
     * @throws ContentStoreException if the space or content does not exist or the content cannot be deleted
     */
    public void deleteContent(String spaceId, String contentId)
            throws ContentStoreException;

    /**
     * Sets the metadata associated with content. This effectively removes all
     * of the current content metadata and adds a new set of metadata. Some
     * metadata, such as system metadata provided by the underlying storage
     * system, cannot be updated or removed. Some of the values which cannot be
     * updated or removed: content-checksum, content-modified, content-size
     *
     * @param spaceId the identifier of the DuraCloud Space
     * @param contentId the identifier of the content item
     * @param contentMetadata a map of metadata values to be linked to this content
     * @throws ContentStoreException if the space or content does not exist or the content metadata cannot be set
     */
    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Map<String, String> contentMetadata)
            throws ContentStoreException;

    /**
     * Retrieves the metadata associated with content. This includes both
     * metadata generated by the underlying storage system as well as
     *
     * @param spaceId the identifier of the DuraCloud Space
     * @param contentId the identifier of the content item
     * @throws ContentStoreException if the space or content does not exist or the content metadata cannot be retrieved
     * @return the map of metadata values linked to the given contentId
     */
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId)
            throws ContentStoreException;
    
}
