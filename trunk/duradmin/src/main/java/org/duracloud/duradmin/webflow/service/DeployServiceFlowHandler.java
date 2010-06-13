/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.webflow.service;

import org.apache.commons.lang.StringUtils;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.NavigationUtils;
import org.duracloud.serviceconfig.Deployment;
import org.duracloud.serviceconfig.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.Message;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeployServiceFlowHandler
        extends AbstractFlowHandler {

    private static final String SUCCESS_OUTCOME = "success";

    private static final String SERVICE = "serviceInfo";
    private static final String DEPLOYMENT = "deployment";

    private static final String SERVICE_ID = "serviceId";

    private static Logger log = LoggerFactory.getLogger(DeployServiceFlowHandler.class);

    private ServicesManager servicesManager;

    public DeployServiceFlowHandler(ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    @Override
    public MutableAttributeMap createExecutionInputMap(HttpServletRequest request) {
        MutableAttributeMap map = super.createExecutionInputMap(request);
        try {
            if (map == null) {
                map = new LocalAttributeMap();
            }

            NavigationUtils.setReturnTo(request, map);

            String serviceIdValue = request.getParameter(SERVICE_ID);
            int serviceId = Integer.valueOf(serviceIdValue);
            ServiceInfo serviceInfo =getService(serviceId);
            map.put(SERVICE, serviceInfo);

            String deploymentIdValue = request.getParameter("deploymentId");
            if(!StringUtils.isBlank(deploymentIdValue)){
                int deploymentId = Integer.valueOf(deploymentIdValue);
                map.put(DEPLOYMENT, getDeployment(serviceInfo, deploymentId));
            }

        } catch (Exception ex) {
            log.error("Error creating execution map", ex);
        }
        return map;
    }

    private Deployment getDeployment(ServiceInfo serviceInfo, int deploymentId) {
        for(Deployment deployment : serviceInfo.getDeployments()){
            if(deployment.getId() == deploymentId){
                return deployment;
            }
        }
        
        //should never happen
        throw new RuntimeException("deployment id "+ deploymentId + " is not associated with " + serviceInfo);
    }

    private ServiceInfo getService(int serviceId) throws Exception {
        ServiceInfo s = servicesManager.getService(serviceId);
        return s;
    }

    public String handleExecutionOutcome(FlowExecutionOutcome outcome,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        String returnTo = NavigationUtils.getReturnTo(outcome);
        ServiceInfo service = (ServiceInfo) outcome.getOutput().get(SERVICE);

        String outcomeUrl = null;

        outcomeUrl = ("contextRelative:/services");

        if (outcome.getId().equals("deployed")) {
            Message message = MessageUtils.createMessage("Successfully deployed service.");
            outcomeUrl =
                    MessageUtils.appendRedirectMessage(outcomeUrl,message,request);
        } else if(outcome.getId().equals("reconfigured")){
            Message message = MessageUtils.createMessage("Successfully reconfigured service.");
            outcomeUrl =
                    MessageUtils.appendRedirectMessage(outcomeUrl,message,request);
            
        } else if (returnTo == null) {
            outcomeUrl = "contextRelative:/services";
        } else {
            outcomeUrl = returnTo;
        }

        return outcomeUrl;
    }

}
