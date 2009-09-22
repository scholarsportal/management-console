package org.duracloud.duradmin.control;

import org.apache.log4j.Logger;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.domain.Service;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class DeployServiceController extends ServicesController {

    protected final Logger log = Logger.getLogger(getClass());

	public DeployServiceController()
	{
        setCommandClass(Service.class);
        setCommandName("service");
	}

    @Override
    protected ModelAndView onSubmit(Object command,
                                    BindException errors)
    throws Exception {
        Service serviceToDeploy = (Service) command;

        ServicesManager servicesManager = getServicesManager();

        // Deploy Service
        try {
            servicesManager.deployService(serviceToDeploy.getServiceId(),
                                          serviceToDeploy.getServiceHost());
        } catch (Exception se) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", se.getMessage());
            return mav;
        }

        return getServices();
    }

}