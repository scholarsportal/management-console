
package org.duracloud.duradmin.services.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.duracloud.client.ServicesManager;
import org.duracloud.serviceconfig.ServiceInfo;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class ServicesController implements Controller {

    protected final Logger log = Logger.getLogger(getClass());
    
	private ServicesManager servicesManager;
    
	public ServicesManager getServicesManager() {
		return servicesManager;
	}

	public void setServicesManager(ServicesManager servicesManager) {
		this.servicesManager = servicesManager;
	}

    
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		if("json".equals(request.getParameter("f"))){
			String method = request.getParameter("method");	
			List<ServiceInfo> services = null;
			if("available".equals(method)){
				services = servicesManager.getAvailableServices();	
			}else{
				services = servicesManager.getDeployedServices();
			}

	        ModelAndView mav = new ModelAndView("jsonView");
			mav.addObject("services",services);
			return mav;
		}else{
	        ModelAndView mav = new ModelAndView("services-manager");
	        return mav;
		}
	}
}