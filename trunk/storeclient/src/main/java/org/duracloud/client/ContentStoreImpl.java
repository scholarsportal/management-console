package org.duracloud.client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.domain.Content;
import org.duracloud.domain.Space;
import org.duracloud.storage.domain.StorageProviderType;
import org.duracloud.storage.provider.StorageProvider;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Provides access to a content store
 *
 * @author Bill Branan
 */
public class ContentStoreImpl implements ContentStore{


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
    public ContentStoreImpl(String baseURL, StorageProviderType type, String storeId) {
        this.baseURL = baseURL;
        this.type = type;
        this.storeId = storeId;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getStorageProviderType() {
        return type.name();
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
    
    @Override
    public List<Space> getSpaces() throws ContentStoreException {
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
                throw new ContentStoreException("Response body is empty");
            }
        } catch (Exception e) {
            throw new ContentStoreException("Could not get spaces due to: " +
                                            e.getMessage(), e);
        }
    }

    @Override
    public Space getSpace(String spaceId) throws ContentStoreException {
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
                throw new ContentStoreException("Response body is empty");
            }

            return space;
        } catch (Exception e) {
            throw new ContentStoreException("Could not get space " + spaceId +
                                            " due to: " + e.getMessage(), e);
        }
    }

    @Override
    public void createSpace(String spaceId, Map<String, String> spaceMetadata)
            throws ContentStoreException {
        String url = buildSpaceURL(spaceId);
        try {
            HttpResponse response =
                restHelper.put(url, null, convertMetadataToHeaders(spaceMetadata));
            checkResponse(response, 201);
        } catch (Exception e) {
            throw new ContentStoreException("Could not create space " + spaceId +
                                            " due to: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteSpace(String spaceId) throws ContentStoreException {
        String url = buildSpaceURL(spaceId);
        try {
            HttpResponse response = restHelper.delete(url);
            checkResponse(response, 200);
        } catch (Exception e) {
            throw new ContentStoreException("Could not delete space " + spaceId +
                                            " due to: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, String> getSpaceMetadata(String spaceId)
            throws ContentStoreException {
        String url = buildSpaceURL(spaceId);
        try {
            HttpResponse response = restHelper.head(url);
            checkResponse(response, 200);
            Map<String, String> metadata = extractMetadataFromHeaders(response);
            return metadata;
        } catch (Exception e) {
            throw new ContentStoreException("Could not get space metadata for space " +
                                            spaceId + " due to: " + e.getMessage(), e);
        }
    }

    @Override
    public void setSpaceMetadata(String spaceId,
                                 Map<String, String> spaceMetadata)
            throws ContentStoreException {
        String url = buildSpaceURL(spaceId);
        Map<String, String> headers = convertMetadataToHeaders(spaceMetadata);
        try {
            HttpResponse response = restHelper.post(url, null, headers);
            checkResponse(response, 200);
        } catch (Exception e) {
            throw new ContentStoreException("Could not create space " + spaceId +
                                            " due to: " + e.getMessage(), e);
        }
    }

    @Override
    public AccessType getSpaceAccess(String spaceId) throws ContentStoreException {
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
                throw new ContentStoreException(error);
            }
        } else {
            throw new ContentStoreException("Could not determine access type for space " +
                                            spaceId +
                                            ". No access type metadata is available.");
        }
    }

    @Override
    public void setSpaceAccess(String spaceId, AccessType spaceAccess)
            throws ContentStoreException {
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(StorageProvider.METADATA_SPACE_ACCESS, spaceAccess.name());
        setSpaceMetadata(spaceId, metadata);
    }

    @Override
    public String addContent(String spaceId,
                             String contentId,
                             InputStream content,
                             long contentSize,
                             String contentMimeType,
                             Map<String, String> contentMetadata)
            throws ContentStoreException {
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
            throw new ContentStoreException("Could not add content " + contentId +
                                            " in space " + spaceId +
                                            " due to: " + e.getMessage(), e);
        }
    }

    @Override
    public Content getContent(String spaceId, String contentId)
            throws ContentStoreException {
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
            throw new ContentStoreException("Could not get content " + contentId +
                                            " from space " + spaceId +
                                            " due to: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteContent(String spaceId, String contentId)
            throws ContentStoreException {
        String url = buildContentURL(spaceId, contentId);
        try {
            HttpResponse response = restHelper.delete(url);
            checkResponse(response, 200);
        } catch (Exception e) {
            throw new ContentStoreException("Could not delete content " + contentId +
                                            " from space " + spaceId +
                                            " due to: " + e.getMessage(), e);
        }
    }

    @Override
    public void setContentMetadata(String spaceId,
                                   String contentId,
                                   Map<String, String> contentMetadata)
            throws ContentStoreException {
        String url = buildContentURL(spaceId, contentId);
        Map<String, String> headers =
            convertMetadataToHeaders(contentMetadata);
        try {
            HttpResponse response = restHelper.post(url,
                                                    null,
                                                    headers);
            checkResponse(response, 200);
        } catch (Exception e) {
            throw new ContentStoreException("Could not udpate content metadata for " +
                                            contentId + " in space " + spaceId +
                                            " due to: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, String> getContentMetadata(String spaceId,
                                                  String contentId)
            throws ContentStoreException {
        String url = buildContentURL(spaceId, contentId);
        try {
            HttpResponse response = restHelper.get(url);
            checkResponse(response, 200);
            return mergeMaps(extractMetadataFromHeaders(response),
                             extractNonMetadataHeaders(response));
        } catch (Exception e) {
            throw new ContentStoreException("Could not get metadata for content " +
                                            contentId + " from space " + spaceId +
                                            " due to: " + e.getMessage(), e);
        }
    }

    private void checkResponse(HttpResponse response, int expectedCode)
            throws ContentStoreException {
        String error = "Could not complete request due to error: ";
        if (response == null) {
            throw new ContentStoreException(error + "Response content was null.");
        }
        if (response.getStatusCode() != expectedCode) {
            throw new ContentStoreException(error + "Response code was " +
                                            response.getStatusCode() +
                                            ", expected value was " +
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
