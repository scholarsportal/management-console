
package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.domain.ServiceCommand;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class UndeployServiceController
        extends BaseCommandController {

    protected final Log log = LogFactory.getLog(getClass());
    private ControllerSupport controllerSupport = new ControllerSupport();
    public UndeployServiceController() {
        setCommandClass(ServiceCommand.class);
        setCommandName("service");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        
        ServiceCommand serviceCommand = (ServiceCommand) command;
        try {
            log.info("attempting to undeploy " + serviceCommand);
            ServicesManager servicesManager = controllerSupport.getServicesManager();
            servicesManager.undeployService(serviceCommand.getServiceInfoId(), 
                                            serviceCommand.getDeploymentId());
            log.info("successfully undeployed " + serviceCommand);
            return new ModelAndView("jsonView","result", "success");
        } catch (Exception se) {
            log.error("failed to deploy " + serviceCommand);
            ModelAndView mav = new ModelAndView("jsonView", "result", "failure");
            mav.addObject("cause", se.getMessage());
            return mav;
        }
    }

}