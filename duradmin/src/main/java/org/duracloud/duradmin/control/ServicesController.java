
package org.duracloud.duradmin.control;

import org.apache.log4j.Logger;
import org.duracloud.duradmin.domain.ServiceCommand;
import org.duracloud.serviceconfig.ServiceInfo;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class ServicesController
        extends BaseFormController {

    protected final Logger log = Logger.getLogger(getClass());

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
        try {
            List<ServiceInfo> services = getDeployedServices();
            ModelAndView mav =  new ModelAndView("services", "serviceInfos", services);
            mav.addObject("availableServiceInfos", getServicesManager().getAvailableServices());
            return mav;
        } catch (Exception se) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", se.getMessage());
            return new ModelAndView("error", "error", se.getMessage());
        }
    }

    private List<ServiceInfo> getDeployedServices() throws Exception {
        return getServicesManager().getDeployedServices();
    }
}