package org.duracloud.duraservice.rest;

import org.apache.commons.httpclient.HttpStatus;
import org.duracloud.common.util.error.DuraCloudException;
import org.duracloud.duraservice.error.NoSuchDeployedServiceException;
import org.duracloud.duraservice.error.NoSuchServiceComputeInstanceException;
import org.duracloud.duraservice.error.NoSuchServiceException;
import org.duracloud.duraservice.rest.RestUtil.RequestContent;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.io.InputStream;
import java.net.URI;

/**
 * Provides interaction with services via REST
 *
 * [POST /services] initialize DuraService
 * [GET /services (and /services?show=available) get list (XML) of all services available for deployment
 * [GET /services?show=deployed] get list (XML) of all deployed services
 * [GET /service/{serviceID}] get a particular service with all of its deployments
 * [GET /service/{serviceID}/{deploymentID}] gets a particular service with a particular deployment
 * [PUT /service/{serviceID}?serviceHost=[host-name] deploy a service
 * [POST /service/{serviceID}/{deploymentID}] update the configuration of a service deployment
 * [DELETE /service/{serviceID}/{deploymentID}] undeploy a service a deployed service
 *
 * @author Bill Branan
 */
@Path("/")
public class ServiceRest extends BaseRest {

    private static enum ServiceList {
        AVAILABLE ("available"),
        DEPLOYED ("deployed");

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
     *   <userStorage>
     *     <host>[USER-STORAGE-HOST-NAME]</host>
     *     <port>[USER-STORAGE-PORT]</port>
     *     <context>[USER-STORAGE-CONTEXT]</context>
     *     <msgBrokerUrl>[USER-STORAGE-MSG-BROKER-URL]</msgBrokerUrl>
     *   </userStorage>
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
     *
     * @return 200 on success
     */
    @Path("/services")
    @POST
    public Response initializeServices() {
        try {
            RestUtil restUtil = new RestUtil();
            RequestContent content = restUtil.getRequestContent(request, headers);
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
     * show=available (default) - Include only the services available for deployment
     * show=deployed - Include only the services which have been deployed
     *
     * @param show determines which services list to retrieve (available or deployed)
     * @return 200 on success with a serialized list of services
     */
    @Path("/services")
    @GET
    public Response getServices(@QueryParam("show")
                                String show) {
        ResponseBuilder response = Response.ok();
        String serviceListXml = null;
        if(show == null ||
           show.equals("") ||
           show.equals(ServiceList.AVAILABLE.type)) {
            serviceListXml = ServiceResource.getAvailableServices();
        } else if(show.equals(ServiceList.DEPLOYED.type)) {
            serviceListXml = ServiceResource.getDeployedServices();
        } else {
            response = Response.serverError();
            response.entity("Invalid Request. Allowed values for show are " +
            		        "'available', and 'deployed'.");
            return response.build();
        }

        if(serviceListXml != null) {
            response.entity(serviceListXml);
        } else {
            response = Response.serverError();
            response.entity("Unable to retrieve services list.");
        }

        return response.build();
    }

    /**
     * Gets a full set of service information, including description,
     * configuration options, and a full listing of deployments. A service
     * does not have to be available for deployment in order to be retrieved
     *
     * @param serviceId the ID of the service to retrieve
     * @return 200 on success with a serialized service
     */
    @Path("/services/{serviceId}")
    @GET
    public Response getService(@PathParam("serviceId")
                               String serviceId) {
        ResponseBuilder response = Response.ok();
        String serviceXml;
        try {
            serviceXml = ServiceResource.getService(serviceId);
        } catch(NoSuchServiceException e) {
            return buildNotFoundResponse(e);
        }

        if(serviceXml != null) {
            response.entity(serviceXml);
        } else {
            response = Response.serverError();
            response.entity("Unable to retrieve service " + serviceId);
        }

        return response.build();
    }

    /**
     * Gets information pertaining to a deployed service.
     * Info includes description, configuration options, and a single
     * deployment, which includes configuration selections which are in use.
     *
     * @param serviceId the ID of the service to retrieve
     * @param deploymentId the ID of the deployment to retrieve
     * @return 200 on success with a serialized service
     */
    @Path("/services/{serviceId}/{deploymentId}")
    @GET
    public Response getDeployedService(@PathParam("serviceId")
                                       String serviceId,
                                       @PathParam("deploymentId")
                                       int deploymentId) {
        ResponseBuilder response = Response.ok();
        String serviceXml;
        try {
            serviceXml =
                ServiceResource.getDeployedService(serviceId, deploymentId);
        } catch(NoSuchDeployedServiceException e) {
            return buildNotFoundResponse(e);
        }

        if(serviceXml != null) {
            response.entity(serviceXml);
        } else {
            response = Response.serverError();
            response.entity("Unable to retrieve service " + serviceId +
                            " with deployment " + deploymentId);
        }

        return response.build();
    }

    /**
     * Deploys, Configures, and Starts a service.
     * It is expected that a call to get the configuration options
     * will be made prior to this call and selections/inputs will
     * be included as xml with this request.
     *
     * @param serviceId the ID of the service to deploy
     * @param serviceHost the server host on which to deploy the service
     * @return 201 on success
     */
    @Path("/services/{serviceId}")
    @PUT
    public Response deployService(@PathParam("serviceId")
                                  String serviceId,
                                  @QueryParam("serviceHost")
                                  String serviceHost) {
        InputStream userConfigXml = getRequestContent();
        try {
            ServiceResource.deployService(serviceId, serviceHost, userConfigXml);
        } catch(NoSuchServiceException e) {
            return buildNotFoundResponse(e);
        } catch(NoSuchServiceComputeInstanceException e) {
            return buildNotFoundResponse(e);
        }

        URI location = uriInfo.getRequestUri();
        return Response.created(location).build();
    }

    /**
     * Re-Configures a deployed service.
     *
     * @param serviceId the ID of the service to reconfigure
     * @param deploymentId the ID of the deployment to reconfigure
     * @return 200 on success
     */
    @Path("/services/{serviceId}/{deploymentId}")
    @POST
    public Response configureService(@PathParam("serviceId")
                                     String serviceId,
                                     @PathParam("deploymentId")
                                     int deploymentId) {
        InputStream userConfigXml = getRequestContent();

        try {
            ServiceResource.updateServiceConfig(serviceId,
                                                deploymentId,
                                                userConfigXml);
        } catch(NoSuchDeployedServiceException e) {
            return buildNotFoundResponse(e);
        }
        return Response.ok().build();
    }

    /**
     * Stops and undeploys a service.
     *
     * @param serviceId the ID of the service to undeploy
     * @param deploymentId the ID of the deployment to undeploy
     * @return 200 on success
     */
    @Path("/services/{serviceId}/{deploymentId}")
    @DELETE
    public Response undeployService(@PathParam("serviceId")
                                    String serviceId,
                                    @PathParam("deploymentId")
                                    int deploymentId) {
        try {
            ServiceResource.undeployService(serviceId, deploymentId);
        } catch(NoSuchDeployedServiceException e) {
            return buildNotFoundResponse(e);
        }
        return Response.ok().build();
    }

    /*
     * Retrieves the content stream from an http request
     */
    private InputStream getRequestContent() {
        try {
            RestUtil restUtil = new RestUtil();
            RequestContent content =
                restUtil.getRequestContent(request, headers);
            return content.getContentStream();
        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve request content");
        }
    }

    private Response buildNotFoundResponse(DuraCloudException e) {
        ResponseBuilder response = Response.status(HttpStatus.SC_NOT_FOUND);
        response.entity(e.getFormatedMessage());
        return response.build();
    }


}