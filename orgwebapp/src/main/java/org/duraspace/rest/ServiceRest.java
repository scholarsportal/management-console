package org.duraspace.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides interaction with services via REST
 *
 * @author Bill Branan
 */
@Path("/service")
public class ServiceRest extends BaseRest {

    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * @see ServiceResource.getServices()
     * @return 200 response with XML listing of services
     */
    @GET
    @Produces(XML)
    public Response getServices() {
        String xml = ServiceResource.getServices();
        return Response.ok(xml, TEXT_XML).build();
    }

    /**
     * @see ServiceResource.getServiceSubscriptions(String)
     * @return 200 response with XML listing of service subscriptions
     */
    @Path("/{customerID}")
    @GET
    @Produces(XML)
    public Response getServiceSubscriptions(@PathParam("customerID")
                                            String customerID) {
        String xml = ServiceResource.getServiceSubscriptions(customerID);
        return Response.ok(xml, TEXT_XML).build();
    }

    /**
     * @see ServiceResource.getServiceConfiguration(String, String)
     * @return 200 response with XML service configuration
     */
    @Path("/{customerID}/{serviceID}")
    @GET
    @Produces(XML)
    public Response getServiceConfiguration(@PathParam("customerID")
                                            String customerID,
                                            @PathParam("serviceID")
                                            String serviceID) {
        String xml = ServiceResource.getServiceConfiguration(customerID, serviceID);
        return Response.ok(xml, TEXT_XML).build();
    }

    /**
     * Subscribes a customer to a service.
     * Service configuration XML is expected as request content.
     *
     * @see ServiceResource.addServiceConfiguration(String, String, String)
     * @return 201 response indicating a successful service addition
     */
    @Path("/{customerID}/{serviceID}")
    @PUT
    @Consumes(XML)
    public Response addServiceSubscription(@PathParam("customerID")
                                           String customerID,
                                           @PathParam("serviceID")
                                           String serviceID) {
        String configurationXML = getRequestXML();
        ServiceResource.addServiceSubscription(customerID, serviceID, configurationXML);
        URI location = uriInfo.getRequestUri();
        return Response.created(location).build();
    }

    /**
     * Updates the configuration of a service.
     * Service configuration XML is expected as request content.
     *
     * @see ServiceResource.addServiceConfiguration(String, String, String)
     * @return 200 response indicating a successful service configuration update
     */
    @Path("/{customerID}/{serviceID}")
    @POST
    @Consumes(XML)
    public Response updateServiceConfiguration(@PathParam("customerID")
                                               String customerID,
                                               @PathParam("serviceID")
                                               String serviceID) {
        String configurationXML = getRequestXML();
        ServiceResource.updateServiceConfiguration(customerID, serviceID, configurationXML);
        String responseText = "Service configuration for " + serviceID +
                              " updated successfully";
        return Response.ok(responseText, TEXT_PLAIN).build();
    }

    /**
     * @see ServiceResource.removeServiceConfiguration(String, String)
     * @return 200 response indicating a successful service removal
     */
    @Path("/{customerID}/{serviceID}")
    @DELETE
    public Response removeServiceSubscription(@PathParam("customerID")
                                              String customerID,
                                              @PathParam("serviceID")
                                              String serviceID) {
        ServiceResource.removeServiceSubscription(customerID, serviceID);
        String responseText = "Subscription for service " + serviceID + " removed";
        return Response.ok(responseText, TEXT_PLAIN).build();
    }

    /**
     * Retrieves the XML contents of the HTTP Request.
     * @return XML text from the request
     */
    private String getRequestXML() {
        try {
            // Optimistically reading input as text
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(request.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

            if(builder.length() > 0) {
                return builder.toString();
            } else {
                return null;
            }
        } catch(IOException e) {
            logger.error("Could not retrieve XML content from request: " +
                         e.getMessage());
            return null;
        }
    }
}
