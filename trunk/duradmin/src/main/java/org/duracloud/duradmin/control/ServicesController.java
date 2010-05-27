package org.duracloud.duradmin.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.domain.ServiceCommand;
import org.duracloud.serviceconfig.ServiceInfo;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;


public class ServicesController extends BaseCommandController{
    
    public ServicesController() {
        setCommandClass(ServiceCommand.class);
        setCommandName("service");
    }
    
    
    
    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        String show = request.getParameter("show");
        String format = request.getParameter("format");
        
        if("json".equals(format)){
            ServicesManager sm = getServicesManager();
            List<ServiceInfo> services = ("deployed".equals(show) ? sm.getDeployedServices(): sm.getAvailableServices());
            
            return new ModelAndView("jsonView", "list", services);
        }else{
            return new ModelAndView("services");
        }
        
    }

}
