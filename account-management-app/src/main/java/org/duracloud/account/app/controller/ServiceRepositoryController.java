/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.model.ServicePlan;
import org.duracloud.account.db.model.ServiceRepository;
import org.duracloud.account.db.model.ServiceRepository.ServiceRepositoryType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 27, 2012
 */

@Controller
@RequestMapping(ServiceRepositoryController.BASE_MAPPING)
public class ServiceRepositoryController extends AbstractRootCrudController<ServiceRepoForm>{
    
    @ModelAttribute("serviceRepositoryTypes")
    public ServiceRepositoryType[] serviceRepositoryTypes(){
        return ServiceRepositoryType.values();
    }

    @ModelAttribute("servicePlans")
    public ServicePlan[] servicePlans(){
        return ServicePlan.values();
    }

    public ServiceRepositoryController() {
        super(ServiceRepoForm.class);
    }

    private static final String RELATIVE_MAPPING = "/servicerepositories";
    public static final String BASE_MAPPING =
        RootConsoleHomeController.BASE_MAPPING + RELATIVE_MAPPING;


    @Override
    protected String getBaseViewId() {
        return super.getBaseView() + RELATIVE_MAPPING;
    }

    @Override
    public ModelAndView get() {
        ModelAndView mav = new ModelAndView(getBaseViewId());
        mav.addObject("serviceRepositories",
                      getRootAccountManagerService().listAllServiceRepositories(null));
        return mav;

    }

    @Override
    protected void create(ServiceRepoForm form) {
        getRootAccountManagerService().createServiceRepository(form.getServiceRepoType(),
                                                               form.getServicePlan(),
                                                               form.getHostName(),
                                                               form.getSpaceId(),
                                                               form.getXmlId(),
                                                               form.getVersion(),
                                                               form.getUserName(),
                                                               form.getPassword());
        
    }

    @Override
    protected Object getEntity(Long id) {
        return getRootAccountManagerService().getServiceRepository(id);
    }

    @Override
    protected ServiceRepoForm loadForm(Object obj) {
        ServiceRepository entity = (ServiceRepository)obj;
        ServiceRepoForm form = form();
        form.setHostName(entity.getHostName());
        form.setSpaceId(entity.getSpaceId());
        form.setXmlId(entity.getServiceXmlId());
        form.setVersion(entity.getVersion());
        form.setUserName(entity.getUsername());
        form.setPassword(entity.getPassword());
        form.setServicePlan(entity.getServicePlan());
        form.setServiceRepoType(entity.getServiceRepositoryType());
        return form;
    }

    @Override
    protected void update(Long id, ServiceRepoForm form) {
        getRootAccountManagerService().editServiceRepository(id,
                                                        form.getServiceRepoType(),
                                                        form.getServicePlan(),
                                                        form.getHostName(),
                                                        form.getSpaceId(),
                                                        form.getXmlId(),
                                                        form.getVersion(),
                                                        form.getUserName(),
                                                        form.getPassword());
        
    }

    @Override
    protected void delete(Long id) {
        getRootAccountManagerService().deleteServiceRepository(id);
    }


}
