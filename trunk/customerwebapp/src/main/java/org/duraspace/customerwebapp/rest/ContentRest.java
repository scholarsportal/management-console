package org.duraspace.customerwebapp.rest;

import java.io.InputStream;

import java.net.URI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.duraspace.common.web.RestResourceException;
import org.duraspace.customerwebapp.rest.RestUtil.RequestContent;
import org.duraspace.storage.provider.StorageProvider;

/**
 * Provides interaction with content via REST
 *
 * @author Bill Branan
 */
@Path("/{spaceID}/{contentID}")
public class ContentRest extends BaseRest {

    /**
     * @see ContentResource.getContent()
     * @see ContentResource.getContentMetadata()
     * @return 200 response with content stream as body and content metadata as headers
     */
    @GET
    public Response getContent(@PathParam("spaceID")
                               String spaceID,
                               @PathParam("contentID")
                               String contentID,
                               @QueryParam("storeID")
                               String storeID) {
        try {
            Map<String, String> metadata =
                ContentResource.getContentMetadata(spaceID, contentID, storeID);
            String mimetype =
                metadata.get(StorageProvider.METADATA_CONTENT_MIMETYPE);
            if(mimetype == null || mimetype.equals("")) {
                mimetype = DEFAULT_MIME;
            }
            InputStream content =
                ContentResource.getContent(spaceID, contentID, storeID);
            return addContentMetadataToResponse(Response.ok(content, mimetype),
                                                metadata);
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * @see ContentResource.getContentMetadata()
     * @return 200 response with content metadata as headers
     */
    @HEAD
    public Response getContentMetadata(@PathParam("spaceID")
                                       String spaceID,
                                       @PathParam("contentID")
                                       String contentID,
                                       @QueryParam("storeID")
                                       String storeID) {
        try {
            Map<String, String> metadata =
                ContentResource.getContentMetadata(spaceID, contentID, storeID);
            return addContentMetadataToResponse(Response.ok(), metadata);
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Adds the metadata of a content item as header values to the response.
     * See http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1
     * for specifics on particular headers.
     */
    private Response addContentMetadataToResponse(ResponseBuilder response,
                                                  Map<String, String> metadata) {
        if(metadata != null) {
            Iterator<String> metadataNames = metadata.keySet().iterator();
            while(metadataNames.hasNext()) {
                String metadataName = (String)metadataNames.next();
                String metadataValue = metadata.get(metadataName);

                // Flags that, when set to true, indicate that the
                // authoritative value for this data has already
                // been set and should not be overwritten
                boolean contentTypeSet = false;
                boolean contentSizeSet = false;
                boolean contentChecksumSet = false;
                boolean contentModifiedSet = false;

                if(metadataName.equals(StorageProvider.METADATA_CONTENT_MIMETYPE)) {
                    response.header(HttpHeaders.CONTENT_TYPE, metadataValue);
                    contentTypeSet = true;
                } else if(metadataName.equals(StorageProvider.METADATA_CONTENT_SIZE)) {
                    response.header(HttpHeaders.CONTENT_LENGTH, metadataValue);
                    contentSizeSet = true;
                } else if(metadataName.equals(StorageProvider.METADATA_CONTENT_CHECKSUM)) {
                    response.header("Content-MD5", metadataValue);
                    response.header(HttpHeaders.ETAG, metadataValue);
                    contentChecksumSet = true;
                } else if(metadataName.equals(StorageProvider.METADATA_CONTENT_MODIFIED)) {
                    response.header(HttpHeaders.LAST_MODIFIED, metadataValue);
                    contentModifiedSet = true;
                } else if((metadataName.equals(HttpHeaders.CONTENT_TYPE) && !contentTypeSet) ||
                          (metadataName.equals(HttpHeaders.CONTENT_LENGTH) && !contentSizeSet) ||
                          (metadataName.equals("Content-MD5") && !contentChecksumSet) ||
                          (metadataName.equals(HttpHeaders.ETAG) && !contentChecksumSet) ||
                          (metadataName.equals(HttpHeaders.LAST_MODIFIED) && !contentModifiedSet)) {
                    response.header(metadataName, metadataValue);
                } else if(metadataName.equals(HttpHeaders.DATE) ||
                          metadataName.equals("Connection")) {
                    // Ignore this value
                } else if(metadataName.equals("Age") ||
                          metadataName.equals(HttpHeaders.CACHE_CONTROL) ||
                          metadataName.equals(HttpHeaders.CONTENT_ENCODING) ||
                          metadataName.equals(HttpHeaders.CONTENT_LANGUAGE) ||
                          metadataName.equals(HttpHeaders.CONTENT_LOCATION) ||
                          metadataName.equals("Content-Range") ||
                          metadataName.equals(HttpHeaders.EXPIRES) ||
                          metadataName.equals(HttpHeaders.LOCATION) ||
                          metadataName.equals("Pragma") ||
                          metadataName.equals("Retry-After") ||
                          metadataName.equals("Server") ||
                          metadataName.equals("Transfer-Encoding") ||
                          metadataName.equals("Upgrade") ||
                          metadataName.equals("Warning")) {
                    // Pass through as a standard http header
                    response.header(metadataName, metadataValue);
                } else {
                    // Custom header, append prefix
                    response.header(HEADER_PREFIX + metadataName, metadataValue);
                }
            }
        }
        return response.build();
    }

    /**
     * @see ContentResource.updateContentMetadata()
     * @return 200 response indicating content metadata updated successfully
     */
    @POST
    public Response updateContentMetadata(@PathParam("spaceID")
                                          String spaceID,
                                          @PathParam("contentID")
                                          String contentID,
                                          @QueryParam("storeID")
                                          String storeID) {
        try {
            MultivaluedMap<String, String> rHeaders = headers.getRequestHeaders();

            String contentName = null;
            if(rHeaders.containsKey(CONTENT_NAME_HEADER)) {
                contentName = rHeaders.getFirst(CONTENT_NAME_HEADER);
            }

            String contentMimeType = null;
            if(rHeaders.containsKey(CONTENT_MIMETYPE_HEADER)) {
                contentMimeType = rHeaders.getFirst(CONTENT_MIMETYPE_HEADER);
            }
            if(contentMimeType == null && rHeaders.containsKey(HttpHeaders.CONTENT_TYPE)) {
                contentMimeType = rHeaders.getFirst(HttpHeaders.CONTENT_TYPE);
            }

            Map<String, String> userMetadata =
                getUserMetadata(CONTENT_NAME_HEADER, CONTENT_MIMETYPE_HEADER);

            ContentResource.updateContentMetadata(spaceID,
                                                  contentID,
                                                  contentName,
                                                  contentMimeType,
                                                  userMetadata,
                                                  storeID);
            String responseText = "Content " + contentID + " updated successfully";
            return Response.ok(responseText, TEXT_PLAIN).build();
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * @see ContentResource.addContent()
     * @return 201 response indicating content added successfully
     */
    @PUT
    public Response addContent(@PathParam("spaceID")
                               String spaceID,
                               @PathParam("contentID")
                               String contentID,
                               @QueryParam("storeID")
                               String storeID) {
        RequestContent content = null;
        try {
            RestUtil restUtil = new RestUtil();
            content = restUtil.getRequestContent(request, headers);
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }

        if(content != null) {
            try {
                String checksum =
                    ContentResource.addContent(spaceID,
                                               contentID,
                                               content.getContentStream(),
                                               content.getMimeType(),
                                               content.getSize(),
                                               storeID);
                updateContentMetadata(spaceID, contentID, storeID);
                URI location = uriInfo.getRequestUri();
                Map<String, String> metadata = new HashMap<String, String>();
                metadata.put(StorageProvider.METADATA_CONTENT_CHECKSUM, checksum);
                return addContentMetadataToResponse(Response.created(location),
                                                    metadata);
            } catch(RestResourceException e) {
                return Response.serverError().entity(e.getMessage()).build();
            }
        } else {
            String error = "Content must be included as part of the request.";
            return Response.status(400).entity(error).build();
        }
    }

    /**
     * @see ContentResource.removeContent()
     * @return 200 response indicating content removed successfully
     */
    @DELETE
    public Response deleteContent(@PathParam("spaceID")
                                  String spaceID,
                                  @PathParam("contentID")
                                  String contentID,
                                  @QueryParam("storeID")
                                  String storeID) {
        try {
            ContentResource.deleteContent(spaceID, contentID, storeID);
            String responseText = "Content " + contentID + " deleted successfully";
            return Response.ok(responseText, TEXT_PLAIN).build();
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

}