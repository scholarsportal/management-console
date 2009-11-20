
package org.duracloud.duradmin.control;

import org.apache.log4j.Logger;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.domain.ServiceCommand;
import org.duracloud.duradmin.util.ServicesUtil;
import org.duracloud.serviceconfig.ServiceInfo;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class ServicesController
        extends BaseFormController {

    protected final Logger log = Logger.getLogger(getClass());

    protected ControllerSupport controllerSupport = new ControllerSupport();

    protected ServicesManager getServicesManager() throws Exception {
        return controllerSupport.getServicesManager();
    }

    public ServicesController() {
        setCommandClass(ServiceCommand.class);
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
        List<ServiceInfo> depServiceList;
        List<ServiceInfo> avlServiceList;
        List<String> serviceHosts;
        try {
            ServicesManager servicesManager = getServicesManager();
            depServiceList = ServicesUtil.getDeployedServices(servicesManager);
            avlServiceList = ServicesUtil.getAvailableServices(servicesManager);

            // FIXME: Each service has its own list of deployment options, so
            //        the list of hosts needs to be specific for each service
            serviceHosts = new ArrayList<String>();
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

    }

}