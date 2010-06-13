/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.control;

import org.duracloud.duradmin.domain.ServiceCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ServiceDeploymentPropertiesController
        extends BaseCommandController {

    protected final Logger log = LoggerFactory.getLogger(ServiceDeploymentPropertiesController.class);

    public ServiceDeploymentPropertiesController() {
        setCommandClass(ServiceCommand.class);
        setCommandName("serviceDeployment");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        ServiceCommand params = (ServiceCommand) command;
        Map<String,String> props = getServicesManager().getDeployedServiceProps(params.getServiceInfoId(),params.getDeploymentId());
        
        List<Map<String,String>> propList = new LinkedList<Map<String,String>>();
        for(String key : props.keySet()){
            Map<String,String> map = new LinkedHashMap<String,String>();
            map.put("name", key);
            map.put("value", props.get(key));
            propList.add(map);
        }
        ModelAndView mav = new ModelAndView();
        mav.setViewName("jsonView");
        mav.getModel().clear();
        mav.addObject("properties", propList);
        return mav;
    }

}