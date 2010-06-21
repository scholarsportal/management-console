
package org.duracloud.duradmin.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.domain.Service;
import org.duracloud.duradmin.util.ServicesUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ServicesController
        extends BaseFormController {

    protected final Logger log = Logger.getLogger(getClass());

    public ServicesController() {
        setCommandClass(Service.class);
        setCommandName("service");
    }

    @Override
    protected boolean isFormSubmission(HttpServletRequest request) {
        return true;
    }
    
    @Override
    protected ModelAndView onSubmit(Object command, BindException errors)
            throws Exception {
        return getServices();
    }

    protected ModelAndView getServices() {
        List<Service> depServiceList;
        List<Service> avlServiceList;
        List<String> serviceHosts;
	/*
        try {
            ServicesManager servicesManager = getServicesManager();
            depServiceList = ServicesUtil.getDeployedServices(servicesManager);
            avlServiceList = ServicesUtil.getAvailableServices(servicesManager);
            serviceHosts = ServicesUtil.getServiceHosts(servicesManager);
        } catch (Exception se) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", se.getMessage());
            return mav;
        }

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("deployedServices", depServiceList);
        mav.addObject("availableServices", avlServiceList);
        mav.addObject("serviceHosts", serviceHosts);
        return mav;
	*/
        return new ModelAndView(getSuccessView());

    }

}
