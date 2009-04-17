package org.duraspace.customerwebapp.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.duraspace.customerwebapp.util.StorageProviderUtil;

/**
 * Provides direct interaction with this instance via REST
 *
 * @author Bill Branan
 */
@Path("/")
public class InstanceRest extends BaseRest {

    /**
     * Initializes the instance
     *
     * @param host - the host on which the main DuraSpace webapp is running
     * @param port - the port on which the main DuraSpace webapp is available
     * @return
     */
    @Path("/initialize")
    @POST
    public Response initializeInstance(@FormParam("host")
                                       String host,
                                       @FormParam("port")
                                       int port){
        try {
            StorageProviderUtil.initialize(host, port);
            String responseText = "Instance initialized";
            return Response.ok(responseText, TEXT_PLAIN).build();
        } catch(Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}