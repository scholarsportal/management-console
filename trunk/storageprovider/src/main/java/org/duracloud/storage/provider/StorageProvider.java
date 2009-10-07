package org.duracloud.storage.provider;

import java.io.InputStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;

/**
 * A Storage Provider provides services which allow content to be
 * stored in and retrieved from spaces.
 *
 * @author Bill Branan
 */
public interface StorageProvider {

    public enum AccessType {OPEN, CLOSED};

    /* Names for space metadata properties */
    public static final String METADATA_SPACE_CREATED = "space-created";
    public static final String METADATA_SPACE_COUNT = "space-count";
    public static final String METADATA_SPACE_ACCESS = "space-access";

    /* Names for content metadata properties */
    public static final String METADATA_CONTENT_MIMETYPE = "content-mimetype";
    public static final String METADATA_CONTENT_SIZE = "content-size";
    public static final String METADATA_CONTENT_CHECKSUM = "content-checksum";
    public static final String METADATA_CONTENT_MODIFIED = "content-modified";

    /* Names values for metadata files */
    public static final String SPACE_METADATA_SUFFIX = "-space-metadata";
    public static final String CONTENT_METADATA_SUFFIX = "-content-metadata";

    /* Other constants */
    public static final String DEFAULT_MIMETYPE = "application/octet-stream";
    public static final DateFormat RFC822_DATE_FORMAT =
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    public static final int HTTP_NOT_FOUND = 404;

    /**
     * Provides a listing of all spaces owned by a customer.
     *
     * @return Iterator listing spaceIds
     */
    public Iterator<String> getSpaces();

    /**
     * Provides a listing of all of the content files within a space.
     *
     * @return Iterator listing contentIds
     */
    public Iterator<String> getSpaceContents(String spaceId);

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
     */
    public void createSpace(String spaceId);

    /**
     * Deletes a space.
     *
     * @param spaceId
     */
    public void deleteSpace(String spaceId);

    /**
     * Retrieves the metadata associated with a space.
     *
     * @param spaceId
     * @return Map of space metadata or null if no metadata exists
     */
    public Map<String, String> getSpaceMetadata(String spaceId);

    /**
     * Sets the metadata associated with a space.
     *
     * @param spaceId
     * @param spaceMetadata
     */
    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata);

    /**
     * Gets the access setting of the space, either OPEN or CLOSED. An OPEN space is
     * available for public viewing. A CLOSED space requires authentication prior to
     * viewing any of the contents.
     *
     * @param spaceId
     * @return
     */
    public AccessType getSpaceAccess(String spaceId);

    /**
     * Sets the accessibility of a space to either OPEN or CLOSED.
     *
     * @param spaceId
     * @param access
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
     */
    public InputStream getContent(String spaceId,
                                  String contentId);

    /**
     * Removes content from a space.
     *
     * @param spaceId
     * @param contentId
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
     * Content-Type
     * Content-MD5
     * ETag
     * Last-Modified
     *
     * @param spaceId
     * @param contentId
     * @param contentMetadata
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
     */
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId);

}
