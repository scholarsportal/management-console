package org.duraspace.rest;

import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Provides interaction with storage provider accounts via REST
 *
 * @author Bill Branan
 */
@Path("/storage")
public class StorageProviderRest extends BaseRest {

    /**
     * @see StorageProviderResource.getStorageProviders()
     * @return 200 response with XML listing of storage providers
     */
    @GET
    @Produces(XML)
    public Response getStorageProviders() {
        String xml = StorageProviderResource.getStorageProviders();
        return Response.ok(xml, TEXT_XML).build();
    }

    /**
     * @see StorageProviderResource.getStorageProviderAccounts(String)
     * @return 200 response with XML listing of storage provider accounts
     */
    @Path("/{customerID}")
    @GET
    @Produces(XML)
    public Response getStorageProviderAccounts(
                     @PathParam("customerID")
                     String customerID) {
        String xml = StorageProviderResource.
            getStorageProviderAccounts(customerID);
        return Response.ok(xml, TEXT_XML).build();
    }

    /**
     * @see StorageProviderResource.getStorageProviderAccount(String, String)
     * @return 200 response with XML storage provider account information
     */
    @Path("/{customerID}/{providerID}")
    @GET
    @Produces(XML)
    public Response getStorageProviderAccount(
                     @PathParam("customerID")
                     String customerID,
                     @PathParam("providerID")
                     String storageProviderID){
        String xml = StorageProviderResource.
            getStorageProviderAccount(customerID, storageProviderID);
        return Response.ok(xml, TEXT_XML).build();
    }

    /**
     * @see StorageProviderResource.addStorageProviderAccount(String, String)
     * @return 201 response with request URI
     */
    @Path("/{customerID}/{providerID}")
    @PUT
    public Response addStorageProviderAccount(
                     @PathParam("customerID")
                     String customerID,
                     @PathParam("providerID")
                     String storageProviderID){
        StorageProviderResource.
            addStorageProviderAccount(customerID, storageProviderID);
        URI location = uriInfo.getRequestUri();
        return Response.created(location).build();
    }

    /**
     * @see StorageProviderResource.closeStorageProviderAccount(String, String);
     * @return 200 response indicating account closed successfully
     */
    @Path("/{customerID}/{providerID}")
    @DELETE
    public Response closeStorageProviderAccount(
                     @PathParam("customerID")
                     String customerID,
                     @PathParam("providerID")
                     String storageProviderID){
        StorageProviderResource.
            closeStorageProviderAccount(customerID, storageProviderID);
        String responseText = "Account for provider " + storageProviderID + " closed";
        return Response.ok(responseText, TEXT_PLAIN).build();
    }

    /**
     * @see StorageProviderResource.setPrimaryStorageProvider(String, String)
     * @return 200 response indicating that provider was set to primary successfully
     */
    @Path("/{customerID}/{providerID}")
    @POST
    public Response setPrimaryStorageProvider(
                     @PathParam("customerID")
                     String customerID,
                     @PathParam("providerID")
                     String storageProviderID){
        StorageProviderResource.
            setPrimaryStorageProvider(customerID, storageProviderID);
        String responseText = "Provider " + storageProviderID + " set to primary";
        return Response.ok(responseText, TEXT_PLAIN).build();
    }
}