package org.duraspace.rest;

import java.io.InputStream;

import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.duraspace.common.web.RestResourceException;
import org.duraspace.rest.RestUtil.RequestContent;
import org.duraspace.storage.StorageProvider;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Provides interaction with content via REST
 *
 * @author Bill Branan
 */
@Path("/content")
public class ContentRest extends BaseRest {

    /**
     * @see ContentResource.getContent()
     * @return 200 response with content stream or content properties
     */
    @Path("/{customerID}/{spaceID}/{contentID}")
    @GET
    public Response getContent(@PathParam("customerID")
                               String customerID,
                               @PathParam("spaceID")
                               String spaceID,
                               @PathParam("contentID")
                               String contentID,
                               @QueryParam("properties")
                               @DefaultValue("false")
                               boolean properties) {
        try {
            String cProperties =
                ContentResource.getContentProperties(customerID, spaceID, contentID);
            if(properties) {
                return Response.ok(cProperties, XML).build();
            } else {
                String mimetype = getMimeType(cProperties);
                InputStream content =
                    ContentResource.getContent(customerID, spaceID, contentID);
                return Response.ok(content, mimetype).build();
            }
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * @see ContentResource.updateContentProperties()
     * @return 200 response indicating content properties updated successfully
     */
    @Path("/{customerID}/{spaceID}/{contentID}")
    @POST
    public Response updateContentProperties(@PathParam("customerID")
                                            String customerID,
                                            @PathParam("spaceID")
                                            String spaceID,
                                            @PathParam("contentID")
                                            String contentID,
                                            @FormParam("contentName")
                                            String contentName,
                                            @FormParam("contentMimeType")
                                            String contentMimeType) {
        try {
            ContentResource.updateContentProperties(customerID,
                                                    spaceID,
                                                    contentID,
                                                    contentName,
                                                    contentMimeType);
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
    @Path("/{customerID}/{spaceID}/{contentID}")
    @PUT
    public Response addContent(@PathParam("customerID")
                               String customerID,
                               @PathParam("spaceID")
                               String spaceID,
                               @PathParam("contentID")
                               String contentID) {
        RequestContent content = null;
        try {
            RestUtil restUtil = new RestUtil();
            content = restUtil.getRequestContent(request, headers);
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }

        try {
            ContentResource.addContent(customerID,
                                       spaceID,
                                       contentID,
                                       content.getContentStream(),
                                       content.getMimeType(),
                                       content.getSize());
            ContentResource.updateContentProperties(customerID,
                                                    spaceID,
                                                    contentID,
                                                    contentID,
                                                    content.getMimeType());
            URI location = uriInfo.getRequestUri();
            return Response.created(location).build();
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * @see ContentResource.removeContent()
     * @return 200 response indicating content removed successfully
     */
    @Path("/{customerID}/{spaceID}/{contentID}")
    @DELETE
    public Response deleteContent(@PathParam("customerID")
                                  String customerID,
                                  @PathParam("spaceID")
                                  String spaceID,
                                  @PathParam("contentID")
                                  String contentID) {
        try {
            ContentResource.deleteContent(customerID, spaceID, contentID);
            String responseText = "Content " + contentID + " deleted successfully";
            return Response.ok(responseText, TEXT_PLAIN).build();
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Retrieves the mime type from content properties
     * @param properties
     * @return
     */
    private String getMimeType(String properties) {
        String mimetype = "application/octet-stream";
        try {
            if(properties != null) {
                SAXBuilder builder = new SAXBuilder();
                Document doc = builder.build(properties);
                Element propElem =
                    doc.getRootElement().getChild("properties");
                Element mimeElem =
                    propElem.getChild(StorageProvider.METADATA_CONTENT_MIMETYPE);
                mimetype = mimeElem.getText();
            }
        } catch(Exception e) {
            mimetype = "application/octet-stream";
        }
        return mimetype;
    }

}