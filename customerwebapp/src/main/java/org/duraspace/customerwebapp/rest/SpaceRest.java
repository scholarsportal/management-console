package org.duraspace.customerwebapp.rest;

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
@Path("/")
public class SpaceRest extends BaseRest {

    /**
     * @see SpaceResource.getSpaces()
     * @return 200 response with XML listing of spaces
     */
    @Path("/spaces")
    @GET
    @Produces(XML)
    public Response getSpaces(@QueryParam("storeID")
                              String storeID) {
        try {
            String xml = SpaceResource.getSpaces(storeID);
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
    @Path("/{spaceID}")
    @GET
    @Produces(XML)
    public Response getSpace(@PathParam("spaceID")
                             String spaceID,
                             @QueryParam("properties")
                             @DefaultValue("false")
                             boolean properties,
                             @QueryParam("storeID")
                             String storeID){
        try {
            String xml = null;
            if(properties) {
                xml = SpaceResource.getSpaceProperties(spaceID, storeID);
            } else {
                xml = SpaceResource.getSpaceContents(spaceID, storeID);
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
    @Path("/{spaceID}")
    @PUT
    public Response addSpace(@PathParam("spaceID")
                             String spaceID,
                             @FormParam("spaceName")
                             String spaceName,
                             @FormParam("spaceAccess")
                             @DefaultValue("CLOSED")
                             String spaceAccess,
                             @QueryParam("storeID")
                             String storeID){
        try {
            SpaceResource.addSpace(spaceID, spaceName, spaceAccess, storeID);
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
    @Path("/{spaceID}")
    @POST
    public Response updateSpaceProperties(@PathParam("spaceID")
                                          String spaceID,
                                          @FormParam("spaceName")
                                          String spaceName,
                                          @FormParam("spaceAccess")
                                          @DefaultValue("CLOSED")
                                          String spaceAccess,
                                          @QueryParam("storeID")
                                          String storeID){
        try {
            SpaceResource.updateSpaceProperties(spaceID, spaceName, spaceAccess, storeID);
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
    @Path("/{spaceID}")
    @DELETE
    public Response deleteSpace(@PathParam("spaceID")
                                String spaceID,
                                @QueryParam("storeID")
                                String storeID){
        try {
            SpaceResource.deleteSpace(spaceID, storeID);
            String responseText = "Space " + spaceID + " deleted successfully";
            return Response.ok(responseText, TEXT_PLAIN).build();
        } catch(RestResourceException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

}