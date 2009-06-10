package org.duraspace.customerwebapp.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.duraspace.customerwebapp.rest.RestUtil.RequestContent;
import org.duraspace.customerwebapp.util.StorageProviderFactory;

/**
 * Provides direct interaction with this instance via REST
 *
 * @author Bill Branan
 */
@Path("/initialize")
public class InstanceRest extends BaseRest {

    /**
     * Initializes the instance. Expects as POST data
     * an XML file which includes credentials for all
     * available storage providers accounts.
     *
     * @return 200 on success
     */
    @POST
    public Response initializeInstance(){
        RequestContent content = null;
        try {
            RestUtil restUtil = new RestUtil();
            content = restUtil.getRequestContent(request, headers);
            StorageProviderFactory.initialize(content.getContentStream());
            String responseText = "Initialization Successful";
            return Response.ok(responseText, TEXT_PLAIN).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}