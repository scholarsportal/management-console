
package org.duracloud.duradmin.webflow.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.client.ServicesManager;
import org.duracloud.client.error.InvalidServiceConfigurationException;
import org.duracloud.client.error.InvalidServiceConfigurationException.ValidationError;
import org.duracloud.client.error.NotFoundException;
import org.duracloud.client.error.ServicesException;
import org.duracloud.duradmin.util.ServiceInfoUtil;
import org.duracloud.serviceconfig.Deployment;
import org.duracloud.serviceconfig.DeploymentOption;
import org.duracloud.serviceconfig.ServiceInfo;
import org.duracloud.serviceconfig.user.UserConfig;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.execution.RequestContext;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class DeployServiceAction
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private static Log log = LogFactory.getLog(DeployServiceAction.class);

    private ServicesManager servicesManager;

    public DeployServiceAction(ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    public DeploymentOption chooseDeploymentOption(RequestContext request)
            throws Exception {
        ServiceInfo serviceInfo = getServiceInfo(request);

        DeploymentOption option =
                getDeployOptionFromRequest(serviceInfo, request
                        .getRequestParameters().get("deploymentOption")
                        .toString());
        request.getFlowScope().put("userConfigs", serviceInfo.getUserConfigs());
        return option;
    }

    private DeploymentOption getDeployOptionFromRequest(ServiceInfo serviceInfo,
                                                        String deploymentOptionString) {

        for (DeploymentOption option : serviceInfo.getDeploymentOptions()) {
            if (option.toString().equals(deploymentOptionString)) {
                return option;
            }
        }

        throw new RuntimeException("unable to find matching deployment option");
    }

    public boolean configureAndDeployService(RequestContext requestContext)
            throws Exception {
        try {
            log.debug("entering...");
            ServiceInfo serviceInfo = getServiceInfo(requestContext);
            DeploymentOption option = getDeploymentOption(requestContext);
            List<UserConfig> userConfigs = serviceInfo.getUserConfigs();
            if(userConfigs != null){
                applyValues(userConfigs, requestContext);
            }
            int deploymentId = deployService(serviceInfo, option);
            requestContext.getFlowScope().put("deploymentId",
                                              new Integer(deploymentId));

               
        } catch (ServicesException e) {
            return handleException(e, requestContext);
        }
        return true;
    }

    private int deployService(ServiceInfo serviceInfo,
                              DeploymentOption deploymentOption)
            throws ServicesException, NotFoundException {
        int id = serviceInfo.getId();
        String version = serviceInfo.getUserConfigVersion();
        String message =
                MessageFormat
                        .format("about to deploy serviceInfo [id={0}, userConfigVersion={1}, deploymentOption[{2}] ",
                                id,
                                version,
                                deploymentOption);

        log.info(message);
        int deploymentId = servicesManager.deployService(id,
                                                         version,
                                                         serviceInfo.getUserConfigs(),
                                                         deploymentOption);
        message =
                MessageFormat
                        .format("deployed service [id={0}, userConfigVersion={1}, deploymentOption[{2},] -  id returned: [{3}] ",
                                id,
                                version,
                                deploymentOption,
                                deploymentId);
        log.info(message);
        return deploymentId;
    }

    @SuppressWarnings("unchecked")
    private void applyValues(List<UserConfig> list,
                             RequestContext requestContext) {
        Map<String, String> parameters =
                (Map<String, String>) requestContext.getRequestParameters().asMap();
        ServiceInfoUtil.applyValues(list, parameters);
    }

    private DeploymentOption getDeploymentOption(RequestContext requestContext) {
        return (DeploymentOption) requestContext.getFlowScope()
                .get("deploymentOption");
    }

    private boolean handleException(ServicesException e,
                                    RequestContext requestContext) {
        MessageContext context = requestContext.getMessageContext();
        if (e instanceof InvalidServiceConfigurationException) {
            InvalidServiceConfigurationException ex =
                    (InvalidServiceConfigurationException) e;
            for (ValidationError error : ex.getErrors()) {
                context.addMessage(
                        new MessageBuilder().error().source(error.getPropertyName()).defaultText(error.getErrorText())
                        .build());
            }
        } else {
            context.addMessage(new MessageBuilder()
                                    .error().defaultText(e.getFormattedMessage())
                                    .build());
        }

        requestContext.getFlashScope().put("errors", 
                                           requestContext.getMessageContext().getAllMessages());
        
        return false;

    }

    public boolean reconfigureServiceDeployment(RequestContext requestContext)
            throws Exception {
        try {
            log.debug("entering...");
            ServiceInfo serviceInfo = getServiceInfo(requestContext);
            Deployment deployment = getDeployment(requestContext);
            List<UserConfig> userConfigs = deployment.getUserConfigs();
            if(userConfigs != null){
                applyValues(userConfigs, requestContext);
            }
            reconfigure(serviceInfo, deployment);
            return true;
        } catch (ServicesException e) {
            return handleException(e, requestContext);
        }
    }

    private Deployment getDeployment(RequestContext requestContext) {
        return (Deployment) requestContext.getFlowScope().get("deployment");
    }

    private void reconfigure(ServiceInfo serviceInfo, Deployment deployment)
        throws ServicesException, NotFoundException {
        int id = serviceInfo.getId();
        String version = serviceInfo.getUserConfigVersion();
        String message =
                MessageFormat
                        .format("updated configuration for serviceInfo(id={0}, userConfigVersion={1}, deploymentOption={2})",
                                id,
                                version,
                                deployment);
        log.info(message);
        servicesManager.updateServiceConfig(id,
                                            deployment.getId(),
                                            version,
                                            deployment.getUserConfigs());
        message =
                MessageFormat
                        .format("updated configuration for serviceInfo(id={0}, userConfigVersion={1}, deploymentOption={2})",
                                id,
                                version,
                                deployment);
        log.info(message);

    }

    private ServiceInfo getServiceInfo(RequestContext requestContext) {
        return (ServiceInfo) requestContext.getFlowScope().get("serviceInfo");
    }
}