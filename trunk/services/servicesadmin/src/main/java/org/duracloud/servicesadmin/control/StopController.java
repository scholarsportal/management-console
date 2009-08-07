
package org.duracloud.servicesadmin.control;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.servicesadmin.util.HttpRequestHelper;
import org.duracloud.servicesutil.util.ServiceStopper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class StopController
        extends AbstractController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ServiceStopper serviceStopper;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response)
            throws Exception {

        String serviceId = HttpRequestHelper.getServiceIdParameter(request);
        getServiceStopper().stop(serviceId);

        ServletOutputStream out = response.getOutputStream();
        out.println("in stop-controller");
        out.close();
        return new ModelAndView();
    }

    public ServiceStopper getServiceStopper() {
        return serviceStopper;
    }

    public void setServiceStopper(ServiceStopper serviceStopper) {
        this.serviceStopper = serviceStopper;
    }

}