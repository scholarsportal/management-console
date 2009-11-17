
package org.duracloud.duradmin.webflow.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.duradmin.control.ControllerSupport;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.NavigationUtils;
import org.duracloud.duradmin.util.ServicesUtil;
import org.duracloud.serviceconfig.ServiceInfo;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.mvc.servlet.AbstractFlowHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

public class DeployServiceFlowHandler
        extends AbstractFlowHandler {

    private static final String SUCCESS_OUTCOME = "success";

    private static final String SERVICE = "service";

    private static final String SERVICE_ID = "serviceId";

    private static Log log = LogFactory.getLog(DeployServiceFlowHandler.class);

    private ControllerSupport controllerSupport = new ControllerSupport();

    @Override
    public MutableAttributeMap createExecutionInputMap(HttpServletRequest request) {
        MutableAttributeMap map = super.createExecutionInputMap(request);
        try {
            if (map == null) {
                map = new LocalAttributeMap();
            }

            NavigationUtils.setReturnTo(request, map);

            String serviceId = request.getParameter(SERVICE_ID);

            map.put(SERVICE, getService(serviceId));
        } catch (Exception ex) {
            log.error(ex);
        }
        return map;
    }

    private ServiceInfo getService(String serviceId) throws Exception {
        ServiceInfo s =
                ServicesUtil.initializeService(controllerSupport
                        .getServicesManager(), serviceId);
        return s;
    }

    public String handleExecutionOutcome(FlowExecutionOutcome outcome,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        String returnTo = NavigationUtils.getReturnTo(outcome);
        ServiceInfo service = (ServiceInfo) outcome.getOutput().get(SERVICE);

        String outcomeUrl = null;

        if (outcome.getId().equals(SUCCESS_OUTCOME)) {
            outcomeUrl =
                    MessageFormat
                            .format("contextRelative:/services.htm?serviceId={0}",
                                    service.getId());
            outcomeUrl =
                    MessageUtils
                            .appendRedirectMessage(outcomeUrl,
                                                   MessageUtils
                                                           .createMessage("Successfully deployed service."),
                                                   request);
        } else if (returnTo == null) {
            outcomeUrl = "contextRelative:/services.htm";
        } else {
            outcomeUrl = returnTo;
        }

        return outcomeUrl;
    }

}
