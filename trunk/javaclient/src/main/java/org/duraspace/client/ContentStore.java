
package org.duraspace.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.domain.Content;
import org.duraspace.domain.Space;
import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.domain.StorageProviderType;
import org.duraspace.storage.provider.StorageProvider;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Provides access to a content store
 *
 * @author Bill Branan
 */
public class ContentStore {

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

    private String storeId = null;

    private StorageProviderType type = null;

    private String baseURL = null;

    private static RestHttpHelper restHelper = new RestHttpHelper();

    private static final String HEADER_PREFIX = "x-dura-meta-";

    /**
     * Creates a ContentStore
     *
     * @param storeID
     */
    public ContentStore(String baseURL, StorageProviderType type, String storeId) {
        this.baseURL = baseURL;
        this.type = type;
        this.storeId = storeId;
    }

    public String getStoreId() {
        return storeId;
    }

    public StorageProviderType getStorageProviderType() {
        return type;
    }

    private String buildURL(String relativeURL) {
        String url = baseURL + relativeURL;
        if (storeId != null && !storeId.equals("")) {
            url += ("?storeID=" + storeId);
        }
        return url;
    }

    private String buildSpaceURL(String spaceId) {
        return buildURL("/" + spaceId);
    }

    private String buildContentURL(String spaceId, String contentId) {
        return buildURL("/" + spaceId + "/" + contentId);
    }

    /**
     * Provides a listing of all spaces. Spaces in the list include metadata but
     * not a listing of content.
     *
     * @return Iterator listing spaceIds
     * @throws StorageException
     */
    public List<Space> getSpaces() throws StorageException {
        String url = buildURL("/spaces");
        try {
            HttpResponse response = restHelper.get(url);
            checkResponse(response, 200);
            String responseText = response.getResponseBody();
            if (responseText != null) {
                List<Space> spaces = new ArrayList<Space>();
                InputStream is =
                        new ByteArrayInputStream(responseText.getBytes());
                SAXBuilder builder = new SAXBuilder();
                Document doc = builder.build(is);
                Element spacesElem = doc.getRootElement();
                Iterator<?> spaceList = spacesElem.getChildren().iterator();
                while (spaceList.hasNext()) {
                    Space space = new Space();
                    Element spaceElem = (Element) spaceList.next();
                    space.setId(spaceElem.getAttributeValue("id"));
                    Iterator<?> spaceMetadata =
                            spaceElem.getChildren().iterator();
                    while (spaceMetadata.hasNext()) {
                        Element metaElem = (Element) spaceMetadata.next();
                        space.addMetadata(metaElem.getName(), metaElem
                                .getTextTrim());
                    }
                    spaces.add(space);
                }
                return spaces;
            } else {
                throw new StorageException("Response body is empty");
            }
        } catch (Exception e) {
            throw new StorageException("Could not get spaces due to: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Provides a Space, including a listing of all of the content files within
     * a space and the metadata associated with the space.
     *
     * @return Space
     * @throws StorageException
     */
    public Space getSpace(String spaceId) throws StorageException {
        String url = buildSpaceURL(spaceId);
        try {
            HttpResponse response = restHelper.get(url);
            checkResponse(response, 200);
            Space space = new Space();
            space.setMetadata(extractMetadataFromHeaders(response));

            String responseText = response.getResponseBody();
            if (responseText != null) {
                InputStream is =
                        new ByteArrayInputStream(responseText.getBytes());
                SAXBuilder builder = new SAXBuilder();
                Document doc = builder.build(is);
                Element spaceElem = doc.getRootElement();

                space.setId(spaceElem.getAttributeValue("id"));
                Iterator<?> spaceContents = spaceElem.getChildren().iterator();
                while (spaceContents.hasNext()) {
                    Element contentElem = (Element) spaceContents.next();
                    space.addContentId(contentElem.getTextTrim());
                }
            } else {
                throw new StorageException("Response body is empty");
            }

            return space;
        } catch (Exception e) {
            throw new StorageException("Could not get space " + spaceId
                    + " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new space. Depending on the storage implementation, the spaceId
     * may be changed somewhat to comply with the naming rules of the underlying
     * storage provider. The same spaceId value used here can be used in all
     * other methods, as the conversion will be applied internally, however a
     * call to getSpaces() may not include a space with exactly this same name.
     *
     * @param spaceId
     * @throws StorageException
     */
    public void createSpace(String spaceId, Map<String, String> spaceMetadata)
            throws StorageException {
        String url = buildSpaceURL(spaceId);
        try {
            HttpResponse response =
                restHelper.put(url, null, convertMetadataToHeaders(spaceMetadata));
            checkResponse(response, 201);
        } catch (Exception e) {
            throw new StorageException("Could not create space " + spaceId +
                                       " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a space.
     *
     * @param spaceId
     * @throws StorageException
     */
    public void deleteSpace(String spaceId) throws StorageException {
        String url = buildSpaceURL(spaceId);
        try {
            HttpResponse response = restHelper.delete(url);
            checkResponse(response, 200);
        } catch (Exception e) {
            throw new StorageException("Could not delete space " + spaceId +
                                       " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the metadata associated with a space.
     *
     * @param spaceId
     * @return Map of space metadata or null if no metadata exists
     * @throws StorageException
     */
    public Map<String, String> getSpaceMetadata(String spaceId)
            throws StorageException {
        String url = buildSpaceURL(spaceId);
        try {
            HttpResponse response = restHelper.head(url);
            checkResponse(response, 200);
            Map<String, String> metadata = extractMetadataFromHeaders(response);
            return metadata;
        } catch (Exception e) {
            throw new StorageException("Could not get space metadata for space " +
                                       spaceId + " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Sets the metadata associated with a space. Only values included
     * in the  metadata map will be updated, others will remain unchanged.
     *
     * @param spaceId
     * @param spaceMetadata
     * @throws StorageException
     */
    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata)
            throws StorageException {
        String url = buildSpaceURL(spaceId);
        Map<String, String> headers = convertMetadataToHeaders(spaceMetadata);
        try {
            HttpResponse response = restHelper.post(url, null, headers);
            checkResponse(response, 200);
        } catch (Exception e) {
            throw new StorageException("Could not create space " + spaceId +
                                       " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the access setting of the space, either OPEN or CLOSED. An OPEN
     * space is available for public viewing. A CLOSED space requires
     * authentication prior to viewing any of the contents.
     *
     * @param spaceId
     * @return
     * @throws StorageException
     */
    public AccessType getSpaceAccess(String spaceId) throws StorageException {
        Map<String, String> spaceMetadata = getSpaceMetadata(spaceId);
        if(spaceMetadata.containsKey(StorageProvider.METADATA_SPACE_ACCESS)) {
            String spaceAccess =
                spaceMetadata.get(StorageProvider.METADATA_SPACE_ACCESS);
            if(spaceAccess.equals(AccessType.OPEN.name())) {
                return AccessType.OPEN;
            } else if(spaceAccess.equals(AccessType.CLOSED.name())) {
                return AccessType.CLOSED;
            } else {
                String error = "Could not determine access type for space " +
                    spaceId + ". Value of access metadata is " + spaceAccess;
                throw new StorageException(error);
            }
        } else {
            throw new StorageException("Could not determine access type for space " +
                                       spaceId +
                                       ". No access type metadata is available.");
        }
    }

    /**
     * Sets the accessibility of a space to either OPEN or CLOSED.
     *
     * @param spaceId
     * @param access
     * @throws StorageException
     */
    public void setSpaceAccess(String spaceId, AccessType spaceAccess)
            throws StorageException {
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(StorageProvider.METADATA_SPACE_ACCESS, spaceAccess.name());
        setSpaceMetadata(spaceId, metadata);
    }

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
     * @throws StorageException
     */
    public String addContent(String spaceId,
                             String contentId,
                             InputStream content,
                             long contentSize,
                             String contentMimeType,
                             Map<String, String> contentMetadata)
            throws StorageException {
        String url = buildContentURL(spaceId, contentId);
        Map<String, String> headers =
            convertMetadataToHeaders(contentMetadata);
        try {
            HttpResponse response = restHelper.put(url,
                                                   content,
                                                   String.valueOf(contentSize),
                                                   contentMimeType,
                                                   headers);
            checkResponse(response, 201);
            Header checksum = response.getResponseHeader("Content-MD5");
            if(checksum == null) {
                checksum = response.getResponseHeader("ETag");
            }
            return checksum.getValue();
        } catch (Exception e) {
            throw new StorageException("Could not add content " + contentId +
                                       " in space " + spaceId +
                                       " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Gets content from a space.
     *
     * @param spaceId
     * @param contentId
     * @return the content stream or null if the content does not exist
     * @throws StorageException
     */
    public Content getContent(String spaceId, String contentId)
            throws StorageException {
        String url = buildContentURL(spaceId, contentId);
        try {
            HttpResponse response = restHelper.get(url);
            checkResponse(response, 200);
            Content content = new Content();
            content.setId(contentId);
            content.setStream(response.getResponseStream());
            content.setMetadata(
                mergeMaps(extractMetadataFromHeaders(response),
                          extractNonMetadataHeaders(response)));
            return content;
        } catch (Exception e) {
            throw new StorageException("Could not get content " + contentId +
                                       " from space " + spaceId +
                                       " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Removes content from a space.
     *
     * @param spaceId
     * @param contentId
     * @throws StorageException
     */
    public void deleteContent(String spaceId, String contentId)
            throws StorageException {
        String url = buildContentURL(spaceId, contentId);
        try {
            HttpResponse response = restHelper.delete(url);
            checkResponse(response, 200);
        } catch (Exception e) {
            throw new StorageException("Could not delete content " + contentId +
                                       " from space " + spaceId +
                                       " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Sets the metadata associated with content. This effectively removes all
     * of the current content metadata and adds a new set of metadata. Some
     * metadata, such as system metadata provided by the underlying storage
     * system, cannot be updated or removed. Some of the values which cannot be
     * updated or removed: content-checksum content-modified content-size
     *
     * @param spaceId
     * @param contentId
     * @param contentMetadata
     * @throws StorageException
     */
    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Map<String, String> contentMetadata)
            throws StorageException {
        String url = buildContentURL(spaceId, contentId);
        Map<String, String> headers =
            convertMetadataToHeaders(contentMetadata);
        try {
            HttpResponse response = restHelper.post(url,
                                                    null,
                                                    headers);
            checkResponse(response, 200);
        } catch (Exception e) {
            throw new StorageException("Could not udpate content metadata for " +
                                       contentId + " in space " + spaceId +
                                       " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the metadata associated with content. This includes both
     * metadata generated by the underlying storage system as well as
     *
     * @param spaceId
     * @param contentId
     * @return
     * @throws StorageException
     */
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId)
            throws StorageException {
        String url = buildContentURL(spaceId, contentId);
        try {
            HttpResponse response = restHelper.get(url);
            checkResponse(response, 200);
            return mergeMaps(extractMetadataFromHeaders(response),
                             extractNonMetadataHeaders(response));
        } catch (Exception e) {
            throw new StorageException("Could not get metadata for content " +
                                       contentId + " from space " + spaceId +
                                       " due to: " + e.getMessage(), e);
        }
    }

    private void checkResponse(HttpResponse response, int expectedCode)
            throws StorageException {
        String error = "Could not complete request due to error: ";
        if (response == null) {
            throw new StorageException(error + "Response content was null.");
        }
        if (response.getStatusCode() != expectedCode) {
            throw new StorageException(error + "Response code was " +
                                       response.getStatusCode()
                                       + ", expected value was " +
                                       expectedCode);
        }
    }

    private Map<String, String> convertMetadataToHeaders(Map<String, String> metadata) {
        if(metadata == null) {
            return null;
        }

        Map<String, String> headers = new HashMap<String, String>();
        Iterator<String> metaNames = metadata.keySet().iterator();
        while(metaNames.hasNext()) {
            String metaName = metaNames.next();
            headers.put(HEADER_PREFIX + metaName, metadata.get(metaName));
        }
        return headers;
    }

    private Map<String, String> extractMetadataFromHeaders(HttpResponse response) {
        Map<String, String> metadata = new HashMap<String, String>();
        for (Header header : response.getResponseHeaders()) {
            String name = header.getName();
            if (name.startsWith(HEADER_PREFIX)) {
                metadata.put(name.substring(HEADER_PREFIX.length()),
                             header.getValue());
            }
        }
        return metadata;
    }

    private Map<String, String> extractNonMetadataHeaders(HttpResponse response) {
        Map<String, String> headers = new HashMap<String, String>();
        for (Header header : response.getResponseHeaders()) {
            String name = header.getName();
            if (!name.startsWith(HEADER_PREFIX)) {
                if(name.equals("Content-Type")) {
                    headers.put(CONTENT_MIMETYPE, header.getValue());
                } else if (name.equals("Content-MD5") ||
                           name.equals("ETag")) {
                    headers.put(CONTENT_CHECKSUM, header.getValue());
                } else if (name.equals("Content-Length")) {
                    headers.put(CONTENT_SIZE, header.getValue());
                } else if (name.equals("Last-Modified")) {
                    headers.put(CONTENT_MODIFIED, header.getValue());
                }
                headers.put(name, header.getValue());
            }
        }
        return headers;
    }

    /**
     * Adds all mappings from map1 into map2. In the case of a conflict the
     * values from map1 will win.
     */
    private Map<String, String> mergeMaps(Map<String, String> map1, Map<String, String> map2) {
        Iterator<String> map1Names = map1.keySet().iterator();
        while(map1Names.hasNext()) {
            String name = map1Names.next();
            map2.put(name, map1.get(name));
        }
        return map2;
    }

}
