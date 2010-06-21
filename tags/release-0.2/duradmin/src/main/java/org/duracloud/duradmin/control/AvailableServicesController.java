
package org.duracloud.duradmin.control;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.domain.ServiceCommand;
import org.duracloud.serviceconfig.ServiceInfo;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class AvailableServicesController
        extends BaseFormController {

    protected final Logger log = Logger.getLogger(getClass());

    protected ControllerSupport controllerSupport = new ControllerSupport();

    protected ServicesManager getServicesManager() throws Exception {
        return controllerSupport.getServicesManager();
    }

    public AvailableServicesController() {
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
        try {
            List<ServiceInfo> services = getAvailableServices();
            return new ModelAndView("availableServices", "serviceInfos", services);
        } catch (Exception se) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", se.getMessage());
            return new ModelAndView("error", "error", se.getMessage());
        }
    }

    private List<ServiceInfo> getAvailableServices() throws Exception {
        return getServicesManager().getAvailableServices();
    }
}