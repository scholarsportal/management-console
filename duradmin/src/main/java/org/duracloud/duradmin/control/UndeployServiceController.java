/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.control;

import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.domain.ServiceCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UndeployServiceController
        extends BaseCommandController {

    protected final Logger log = LoggerFactory.getLogger(UndeployServiceController.class);
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
            ServicesManager servicesManager = getServicesManager();
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