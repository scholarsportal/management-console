package org.duraspace.rest;

import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.duraspace.common.web.RestResourceException;

/**
 * Provides interaction with spaces via REST
 *
 * @author Bill Branan
 */
@Path("/space")
public class SpaceRest extends BaseRest {

    /**
     * @see SpaceResource.getSpaces()
     * @return 200 response with XML listing of spaces
     */
    @Path("/{customerID}")
    @GET
    @Produces(XML)
    public Response getSpaces(@PathParam("customerID")
                              String customerID) {
        try {
            String xml = SpaceResource.getSpaces(customerID);
            return Response.ok(xml, TEXT_XML).build();
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * @see SpaceResource.getSpaceProperties(String, String);
     * @see SpaceResource.getSpaceContents(String, String);
     * @return 200 response with XML listing of space properties or content
     */
    @Path("/{customerID}/{spaceID}")
    @GET
    @Produces(XML)
    public Response getSpace(@PathParam("customerID")
                             String customerID,
                             @PathParam("spaceID")
                             String spaceID,
                             @QueryParam("properties")
                             @DefaultValue("false")
                             boolean properties){
        try {
            String xml = null;
            if(properties) {
                xml = SpaceResource.getSpaceProperties(customerID, spaceID);
            } else {
                xml = SpaceResource.getSpaceContents(customerID, spaceID);
            }
            return Response.ok(xml, TEXT_XML).build();
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * @see SpaceResource.addSpace(String, String, String, String)
     * @return 201 response with request URI
     */
    @Path("/{customerID}/{spaceID}")
    @PUT
    public Response addSpace(@PathParam("customerID")
                             String customerID,
                             @PathParam("spaceID")
                             String spaceID,
                             @FormParam("spaceName")
                             String spaceName,
                             @FormParam("spaceAccess")
                             @DefaultValue("CLOSED")
                             String spaceAccess){
        try {
            SpaceResource.addSpace(customerID, spaceID, spaceName, spaceAccess);
            URI location = uriInfo.getRequestUri();
            return Response.created(location).build();
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * @see SpaceResource.updateSpaceProperties(String, String, String, String);
     * @return 200 response with XML listing of space properties
     */
    @Path("/{customerID}/{spaceID}")
    @POST
    public Response updateSpaceProperties(@PathParam("customerID")
                                          String customerID,
                                          @PathParam("spaceID")
                                          String spaceID,
                                          @FormParam("spaceName")
                                          String spaceName,
                                          @FormParam("spaceAccess")
                                          @DefaultValue("CLOSED")
                                          String spaceAccess){
        try {
            SpaceResource.updateSpaceProperties(customerID, spaceID, spaceName, spaceAccess);
            String responseText = "Space " + spaceID + " updated successfully";
            return Response.ok(responseText, TEXT_PLAIN).build();
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * @see SpaceResource.deleteSpace(String, String);
     * @return 200 response indicating space deleted successfully
     */
    @Path("/{customerID}/{spaceID}")
    @DELETE
    public Response deleteSpace(@PathParam("customerID")
                                String customerID,
                                @PathParam("spaceID")
                                String spaceID){
        try {
            SpaceResource.deleteSpace(customerID, spaceID);
            String responseText = "Space " + spaceID + " deleted successfully";
            return Response.ok(responseText, TEXT_PLAIN).build();
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}