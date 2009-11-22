
package org.duracloud.duradmin.webflow.service;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.serviceconfig.Deployment;
import org.duracloud.serviceconfig.DeploymentOption;
import org.duracloud.serviceconfig.ServiceInfo;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.execution.RequestContext;

public class DeployServiceAction
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private static Log log = LogFactory.getLog(DeployServiceAction.class);

    public DeploymentOption chooseDeploymentOption(RequestContext request)
    throws Exception {
        ServiceInfo serviceInfo = getServiceInfo(request);
        DeploymentOption option =  serviceInfo.getDeploymentOptions().get(0);
        request.getFlowScope().put("userConfigs", serviceInfo.getUserConfigs());
        return option;
    }

    
    public boolean configureAndDeployService(ServiceInfo serviceInfo, DeploymentOption option, MessageContext messageContext)
            throws Exception {
        log.debug("entering configure And Deploy" + serviceInfo + ", " + option);
        return true;
    }




    public boolean reconfigureServiceDeployment(ServiceInfo serviceInfo, Deployment deployment, MessageContext messageContext)
            throws Exception {
        log.debug("entering reconfigure" + serviceInfo + ", deployment: " + deployment);
        return true;
    }
    
    private ServiceInfo getServiceInfo(RequestContext requestContext){
        return (ServiceInfo)requestContext.getFlowScope().get("serviceInfo");
    }
}