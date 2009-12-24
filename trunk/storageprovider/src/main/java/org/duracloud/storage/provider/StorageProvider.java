package org.duracloud.storage.provider;

import org.duracloud.storage.error.StorageException;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

/**
 * A Storage Provider provides services which allow content to be
 * stored in and retrieved from spaces.
 *
 * @author Bill Branan
 */
public interface StorageProvider {

    public enum AccessType {OPEN, CLOSED}

    /* Names for space metadata properties */
    public static final String METADATA_SPACE_CREATED = "space-created";
    public static final String METADATA_SPACE_COUNT = "space-count";
    public static final String METADATA_SPACE_ACCESS = "space-access";

    /* Names for content metadata properties */
    public static final String METADATA_CONTENT_MIMETYPE = "content-mimetype";
    public static final String METADATA_CONTENT_SIZE = "content-size";
    public static final String METADATA_CONTENT_CHECKSUM = "content-checksum";
    public static final String METADATA_CONTENT_MODIFIED = "content-modified";

    /* Names for reserved metadata properties */
    public static final String METADATA_CONTENT_MD5 = "content-md5";

    /* Names values for metadata files */
    public static final String SPACE_METADATA_SUFFIX = "-space-metadata";
    public static final String CONTENT_METADATA_SUFFIX = "-content-metadata";

    /* Other constants */
    public static final String DEFAULT_MIMETYPE = "application/octet-stream";
    public static final DateFormat RFC822_DATE_FORMAT =
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    public static final int HTTP_NOT_FOUND = 404;

    public static final long DEFAULT_MAX_RESULTS = 1000;

    /**
     * Provides a listing of all spaces owned by a customer.
     *
     * @return Iterator listing spaceIds
     */
    public Iterator<String> getSpaces();

    /**
     * Provides access to the content files within a space. Chunking of the
     * list is handled internally. Prefix can be set to return only content
     * IDs starting with the prefix value.
     *
     * @param spaceId
     * @param prefix - The prefix of the content id (null for no constraints)
     * @return Iterator of contentIds
     * @throws StorageException if space with ID spaceId does not exist
     */
    public Iterator<String> getSpaceContents(String spaceId,
                                             String prefix);

    /**
     * Provides a listing of the content files within a space. The number of
     * items returned is limited to maxResults (default is 1000). Retrieve
     * further results by including the last content ID in the previous list
     * as the marker. Set prefix to return only content IDs starting with the
     * prefix value.
     *
     * @param spaceId
     * @param prefix - Only retrieve content IDs with this prefix (null for all content ids)
     * @param maxResults - The maximum number of content IDs to return in the list (0 indicates default (1000))
     * @param marker - The content ID marking the last item in the previous set (null indicates the first set of ids)
     * @return List of contentIds
     * @throws StorageException if space with ID spaceId does not exist
     */
    public List<String> getSpaceContentsChunked(String spaceId,
                                                String prefix,
                                                long maxResults,
                                                String marker);

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
     * @throws StorageException if space with ID spaceId already exists
     */
    public void createSpace(String spaceId);

    /**
     * Deletes a space.
     *
     * @param spaceId
     * @throws StorageException if space with ID spaceId does not exist
     */
    public void deleteSpace(String spaceId);

    /**
     * Retrieves the metadata associated with a space.
     *
     * @param spaceId
     * @return Map of space metadata or null if no metadata exists
     * @throws StorageException if space with ID spaceId does not exist
     */
    public Map<String, String> getSpaceMetadata(String spaceId);

    /**
     * Sets the metadata associated with a space.
     *
     * @param spaceId
     * @param spaceMetadata
     * @throws StorageException if space with ID spaceId does not exist
     */
    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata);

    /**
     * Gets the access setting of the space, either OPEN or CLOSED. An OPEN space is
     * available for public viewing. A CLOSED space requires authentication prior to
     * viewing any of the contents.
     *
     * @param spaceId
     * @return the access type of the space, OPEN or CLOSED
     * @throws StorageException if space with ID spaceId does not exist
     */
    public AccessType getSpaceAccess(String spaceId);

    /**
     * Sets the accessibility of a space to either OPEN or CLOSED.
     *
     * @param spaceId
     * @param access
     * @throws StorageException if space with ID spaceId does not exist
     */
    public void setSpaceAccess(String spaceId,
                               AccessType access);

    /**
     * Adds content to a space. Computes the checksum of the
     * provided content and checks this against the checksum
     * of the uploaded content to protect against loss or
     * corruption during transfer.
     *
     * @param spaceId
     * @param contentId
     * @param content
     * @return The checksum of the provided content
     * @throws StorageException if space with ID spaceId does not exist
     */
    public String addContent(String spaceId,
                             String contentId,
                             String contentMimeType,
                             long contentSize,
                             InputStream content);

    /**
     * Gets content from a space.
     *
     * @param spaceId
     * @param contentId
     * @return the content stream or null if the content does not exist
     * @throws StorageException if space with ID spaceId does not exist
     */
    public InputStream getContent(String spaceId,
                                  String contentId);

    /**
     * Removes content from a space.
     *
     * @param spaceId
     * @param contentId
     * @throws StorageException if space with ID spaceId does not exist
     */
    public void deleteContent(String spaceId,
                              String contentId);

    /**
     * Sets the metadata associated with content. This effectively
     * removes all of the current content metadata and adds a new
     * set of metadata. Some metadata, such as system metadata
     * provided by the underlying storage system, cannot be updated
     * or removed.
     *
     * Some of the values which cannot be updated or removed:
     * Content-MD5
     * ETag
     * Last-Modified
     *
     * Content-Type cannot be removed, but it can be updated
     *
     * @param spaceId
     * @param contentId
     * @param contentMetadata
     * @throws StorageException if space with ID spaceId does not exist
     */
    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Map<String, String> contentMetadata);

    /**
     * Retrieves the metadata associated with content. This includes
     * both metadata generated by the underlying storage system as
     * well as
     *
     * @param spaceId
     * @param contentId
     * @return
     * @throws StorageException if space with ID spaceId does not exist
     */
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId);

}
