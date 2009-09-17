package org.duracloud.duraservice.rest;

import java.net.URI;

import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.duracloud.common.util.SerializationUtil;
import org.duracloud.duraservice.domain.ServiceException;
import org.duracloud.duraservice.rest.RestUtil.RequestContent;

/**
 * Provides interaction with services via REST
 *
 * [POST /services] initialize DuraService
 * [GET /services (and /services?show=all) get full list (XML) of services (deployed and not)
 * [GET /services?show=available] get list (XML) of all services available for deployment
 * [GET /services?show=deployed] get list (XML) of all deployed services
 * [GET /service/ID] get the status of a particular service
 * [PUT /service/ID?serviceHost=[host-name] deploy a service
 * [POST /service/ID] configure a service
 * [DELETE /service/ID] undeploy a service
 *
 * @author Bill Branan
 */
@Path("/")
public class ServiceRest extends BaseRest {

    private static enum ServiceList {
        AVAILABLE ("available"),
        DEPLOYED ("deployed"),
        ALL ("all");

        public String type;

        ServiceList(String type) {
            this.type = type;
        }
    }

    /**
     * Initializes DuraService.
     * POST content should be similar to:
     *
     * <servicesConfig>
     *   <serviceStorage>
     *     <host>[SERVICES-STORAGE-HOST-NAME]</host>
     *     <port>[SERVICES-STORAGE-PORT]</port>
     *     <context>[SERVICES-STORAGE-CONTEXT]</context>
     *     <spaceId>[SERVICES-STORAGE-SPACE-ID]</spaceId>
     *   </serviceStorage>
     *   <serviceCompute>
     *     <type>AMAZON_EC2</type>
     *     <imageId>[MACHINE-IMAGE-ID]</imageId>
     *     <computeProviderCredential>
     *       <username>[USERNAME]</username>
     *       <password>[PASSWORD]</password>
     *     </computeProviderCredential>
     *   </serviceCompute>
     * </servicesConfig>
     */
    @Path("/services")
    @POST
    public Response initializeServices() {
        RequestContent content = null;
        try {
            RestUtil restUtil = new RestUtil();
            content = restUtil.getRequestContent(request, headers);
            ServiceResource.configureManager(content.getContentStream());
            String responseText = "Initialization Successful";
            return Response.ok(responseText, TEXT_PLAIN).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Gets a listing of services. Use the show parameter to specify which sets
     * should be included in the results:
     * show=available - Include only the services available for deployment
     * show=deployed - Include only the services which are available for deployment
     * show=all (default) - Include both available and deployed services
     *
     * @return
     */
    @Path("/services")
    @GET
    public Response getServices(@QueryParam("show")
                                String show) {
        ResponseBuilder response = Response.ok();
        List<String> serviceList = null;
        if(show == null || show.equals("") || show.equals(ServiceList.ALL.type)) {
            serviceList = ServiceResource.getAllServices();
        } else if(show.equals(ServiceList.DEPLOYED.type)) {
            serviceList = ServiceResource.getDeployedServices();
        } else if(show.equals(ServiceList.AVAILABLE.type)) {
            serviceList = ServiceResource.getAvailableServices();
        } else {
            response = Response.serverError();
            response.entity("Invalid Request. Allowed values for show are " +
            		        "'all', 'available', and 'deployed'.");
            return response.build();
        }

        if(serviceList != null) {
            String xml = SerializationUtil.serializeList(serviceList);
            response.entity(xml);
        } else {
            response = Response.serverError();
            response.entity("Unable to retrieve services list.");
        }

        return response.build();
    }

    /**
     * Gets information about a service.
     *
     * @return
     */
    @Path("/services/{serviceId}")
    @GET
    public Response getService(@PathParam("serviceId")
                               String serviceId) {
        ResponseBuilder response = Response.ok();
        Map<String, String> serviceStatus = null;
        try {
            serviceStatus = ServiceResource.getService(serviceId);
        } catch(ServiceException se) {
            String error = "Could not get service " + serviceId +
                           " due to error: " + se.getMessage();
            response = Response.serverError();
            response.entity(error);
            return response.build();
        }

        if(serviceStatus != null) {
            String xml = SerializationUtil.serializeMap(serviceStatus);
            response.entity(xml);
        } else {
            response = Response.serverError();
            response.entity("Unable to retrieve service.");
        }

        return response.build();
    }

    /**
     * Starts a service.
     *
     * @return
     */
    @Path("/services/{serviceId}")
    @PUT
    public Response deployService(@PathParam("serviceId")
                                  String serviceId,
                                  @QueryParam("serviceHost")
                                  String serviceHost) {
        try {
            ServiceResource.deployService(serviceId, serviceHost);
        } catch(ServiceException se) {
            String error = "Could not deploy service " + serviceId +
                           " to host " + serviceHost +
                           " due to error: " + se.getMessage();
            return Response.serverError().entity(error).build();
        }
        URI location = uriInfo.getRequestUri();
        return Response.created(location).build();
    }

    /**
     * Sets the configuration of a service.
     * POST content should be similar to:
     *
     * <serviceConfig>
     *   <configItem>
     *     <name>property1</name>
     *     <value>value1</value>
     *   </configItem>
     *   <configItem>
     *     <name>property2</name>
     *     <value>value2</value>
     *   </configItem>
     * </serviceConfig>
     */
    @Path("/services/{serviceId}")
    @POST
    public Response configureService(@PathParam("serviceId")
                                     String serviceId) {
        RequestContent content;
        try {
            RestUtil restUtil = new RestUtil();
            content = restUtil.getRequestContent(request, headers);
        } catch(Exception e) {
            String error = "Could not retrieve configuration stream " +
                           "due to error: " + e.getMessage();
            return Response.serverError().entity(error).build();
        }

        try {
            ServiceResource.configureService(serviceId,
                                             content.getContentStream());
        } catch(ServiceException se) {
            String error = "Could not configure service " + serviceId +
                           " due to error: " + se.getMessage();
            return Response.serverError().entity(error).build();
        }
        return Response.ok().build();
    }

    /**
     * Stops a service.
     *
     * @return
     */
    @Path("/services/{serviceId}")
    @DELETE
    public Response undeployService(@PathParam("serviceId")
                                    String serviceId) {
        try {
            ServiceResource.undeployService(serviceId);
        } catch(ServiceException se) {
            String error = "Could not undeploy service " + serviceId +
                           " due to error: " + se.getMessage();
            return Response.serverError().entity(error).build();
        }
        return Response.ok().build();
    }

    /**
     * Gets a list of service hosts.
     *
     * @return
     */
    @Path("/serviceHosts")
    @GET
    public Response getServiceHosts() {
        ResponseBuilder response = Response.ok();
        try {
            List<String> serviceHosts = ServiceResource.getServiceHosts();

            if(serviceHosts != null) {
                String xml = SerializationUtil.serializeList(serviceHosts);
                response.entity(xml);
            } else {
                response = Response.serverError();
                response.entity("Unable to retrieve services host list.");
            }
        } catch(ServiceException se) {
            String error = "Could not get service hosts" +
                           " due to error: " + se.getMessage();
            return Response.serverError().entity(error).build();
        }
        return response.build();
    }

}