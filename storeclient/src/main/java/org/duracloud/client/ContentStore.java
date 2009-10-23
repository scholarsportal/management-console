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

    public static final String SPACE_CREATED =
            StorageProvider.METADATA_SPACE_CREATED;

    public static final String SPACE_COUNT =
            StorageProvider.METADATA_SPACE_COUNT;

    public static final String SPACE_ACCESS =
            StorageProvider.METADATA_SPACE_ACCESS;

    public static final String CONTENT_MIMETYPE =
            StorageProvider.METADATA_CONTENT_MIMETYPE;

    public static final String CONTENT_SIZE =
            StorageProvider.METADATA_CONTENT_SIZE;

    public static final String CONTENT_CHECKSUM =
            StorageProvider.METADATA_CONTENT_CHECKSUM;

    public static final String CONTENT_MODIFIED =
            StorageProvider.METADATA_CONTENT_MODIFIED;


    public String getBaseURL();
    
    public String getStoreId();

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
     * @return Space
     * @throws ContentStoreException if the space does not exist or cannot be retrieved
     */
    public Space getSpace(String spaceId) throws ContentStoreException;
    
    /**
     * Creates a new space. Depending on the storage implementation, the spaceId
     * may be changed somewhat to comply with the naming rules of the underlying
     * storage provider. The same spaceId value used here can be used in all
     * other methods, as the conversion will be applied internally, however a
     * call to getSpaces() may not include a space with exactly this same name.
     *
     * @param spaceId
     * @throws ContentStoreException if the space already exists or cannot be created
     */
    public void createSpace(String spaceId, Map<String, String> spaceMetadata)
            throws ContentStoreException;
    

    /**
     * Deletes a space.
     *
     * @param spaceId
     * @throws ContentStoreException if the space does not exist or cannot be deleted
     */
    public void deleteSpace(String spaceId) throws ContentStoreException;
    
    /**
     * Retrieves the metadata associated with a space.
     *
     * @param spaceId
     * @return Map of space metadata or null if no metadata exists
     * @throws ContentStoreException if the space does not exist or the metadata cannot be retrieved
     */
    public Map<String, String> getSpaceMetadata(String spaceId)
            throws ContentStoreException;
    
    /**
     * Sets the metadata associated with a space. Only values included
     * in the  metadata map will be updated, others will remain unchanged.
     *
     * @param spaceId
     * @param spaceMetadata
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
     * @param spaceId
     * @return AccessType - OPEN or CLOSED
     * @throws ContentStoreException if the space does not exist or access cannot be retrieved
     */
    public AccessType getSpaceAccess(String spaceId) throws ContentStoreException;
    
    /**
     * Sets the accessibility of a space to either OPEN or CLOSED.
     *
     * @param spaceId
     * @param spaceAccess
     * @throws ContentStoreException if the space does not exist or access cannot be set
     */
    public void setSpaceAccess(String spaceId, AccessType spaceAccess)
            throws ContentStoreException;
    
    /**
     * Adds content to a space.
     * Returns the checksum of the content as computed by the
     * underlying storage provider to facilitate comparison
     *
     * @param spaceId
     * @param contentId
     * @param content
     * @param contentMimeType
     * @param contentSize
     * @param contentMetadata
     * @return
     * @throws ContentStoreException if the space does not exist or content cannot be added
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
     * @param spaceId
     * @param contentId
     * @return the content stream
     * @throws ContentStoreException if the space or content does not exist or the content cannot be retrieved
     */
    public Content getContent(String spaceId, String contentId)
            throws ContentStoreException;

    /**
     * Removes content from a space.
     *
     * @param spaceId
     * @param contentId
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
     * @param spaceId
     * @param contentId
     * @param contentMetadata
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
     * @param spaceId
     * @param contentId
     * @return
     * @throws ContentStoreException if the space or content does not exist or the content metadata cannot be retrieved
     */
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId)
            throws ContentStoreException;
    
}
