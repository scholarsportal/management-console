/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.common.domain.ServiceRepository.ServiceRepositoryType;
import org.duracloud.account.util.RootAccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@Lazy
public class ManageServiceReposController  extends AbstractController {
    public static final String REDIRECT_USERS_MANAGE = "/users/manage";

    public static final String MANAGE_SERVICE_REPOS = "/manage/serviceRepo";

    public static final String MANAGE_SERVICE_REPO_NEW_MAPPING = MANAGE_SERVICE_REPOS + NEW_MAPPING;

    public static final String DELETE_MAPPING =
        MANAGE_SERVICE_REPOS + "/byid/{repoId}/delete";
    public static final String EDIT_MAPPING =
        MANAGE_SERVICE_REPOS + "/byid/{repoId}/edit";

    public static final String NEW_FORM_KEY = "serviceRepoForm";
    public static final String NEW_VIEW = "serviceRepo-new";
    public static final String EDIT_VIEW = "serviceRepo-edit";

    @Autowired(required = true)
    protected RootAccountManagerService rootAccountManagerService;

    public void setRootAccountManagerService(
        RootAccountManagerService rootAccountManagerService) {
        this.rootAccountManagerService = rootAccountManagerService;
    }

    @RequestMapping(value = MANAGE_SERVICE_REPO_NEW_MAPPING, method = RequestMethod.GET)
    public String openAddForm(Model model)
        throws Exception {
        log.info("serving up new ServiceRepoForm");

        model.addAttribute(NEW_FORM_KEY, new ServiceRepoForm());

        return NEW_VIEW;
    }

    @ModelAttribute("servicePlans")
    public List<String> getServicePlans() {
        List<String> plans = new ArrayList<String>();
        for (ServicePlan pType : ServicePlan.values()) {
            plans.add(pType.toString());
        }
        return plans;
    }

    @ModelAttribute("serviceRepoTypes")
    public List<String> getServiceRepoTypes() {
        List<String> repoType = new ArrayList<String>();
        for (ServiceRepositoryType rType : ServiceRepositoryType.values()) {
            repoType.add(rType.toString());
        }
        return repoType;
    }

    @RequestMapping(value = { MANAGE_SERVICE_REPO_NEW_MAPPING }, method = RequestMethod.POST)
    public ModelAndView add(
        @ModelAttribute(NEW_FORM_KEY) @Valid ServiceRepoForm serviceRepoForm,
        BindingResult result, Model model) throws Exception {
        if (!result.hasErrors()) {
            //Add new Service Repo
            rootAccountManagerService.createServiceRepository(
                ServiceRepositoryType.valueOf(serviceRepoForm.getServiceRepoType()),
                ServicePlan.fromString(serviceRepoForm.getServicePlan()),
                serviceRepoForm.getHostName(),
                serviceRepoForm.getSpaceId(),
                serviceRepoForm.getXmlId(),
                serviceRepoForm.getVersion(),
                serviceRepoForm.getUserName(),
                serviceRepoForm.getPassword());

            return createRedirectMav(REDIRECT_USERS_MANAGE);
        }

        return new ModelAndView(NEW_VIEW);
    }



    @RequestMapping(value = DELETE_MAPPING, method = RequestMethod.POST)
    public ModelAndView delete(
        @PathVariable int repoId, Model model)
        throws Exception {
        log.info("delete service repo {}", repoId);

        //delete service repo
        rootAccountManagerService.deleteServiceRepository(repoId);

        return createRedirectMav(REDIRECT_USERS_MANAGE);
    }

    @RequestMapping(value = EDIT_MAPPING, method = RequestMethod.GET)
    public String getEditForm(@PathVariable int repoId, Model model)
        throws Exception {
        log.info("getEditForm repo {}", repoId);
        ServiceRepoForm form = new ServiceRepoForm();

        //Fill form
        ServiceRepository serviceRepo =
            rootAccountManagerService.getServiceRepository(repoId);
        form.setHostName(serviceRepo.getHostName());
        form.setSpaceId(serviceRepo.getSpaceId());
        form.setXmlId(serviceRepo.getServiceXmlId());
        form.setVersion(serviceRepo.getVersion());
        form.setUserName(serviceRepo.getUsername());
        form.setPassword(serviceRepo.getPassword());
        form.setServicePlan(serviceRepo.getServicePlan().getText());
        form.setServiceRepoType(serviceRepo.getServiceRepositoryType().name());

        model.addAttribute(NEW_FORM_KEY, form);

        return EDIT_VIEW;
    }

    @RequestMapping(value = EDIT_MAPPING, method = RequestMethod.POST)
    public ModelAndView edit(@PathVariable int repoId,
                       @ModelAttribute(NEW_FORM_KEY) @Valid ServiceRepoForm serviceRepoForm,
					   BindingResult result,
					   Model model) throws Exception {
        log.debug("editRepo repo {}", repoId);

        if (!result.hasErrors()) {
            //Update repo info
            rootAccountManagerService.editServiceRepository(repoId,
                                                            ServiceRepositoryType.valueOf(serviceRepoForm.getServiceRepoType()),
                                                            ServicePlan.fromString(serviceRepoForm.getServicePlan()),
                                                            serviceRepoForm.getHostName(),
                                                            serviceRepoForm.getSpaceId(),
                                                            serviceRepoForm.getXmlId(),
                                                            serviceRepoForm.getVersion(),
                                                            serviceRepoForm.getUserName(),
                                                            serviceRepoForm.getPassword());

            return createRedirectMav(REDIRECT_USERS_MANAGE);
        }

        return new ModelAndView(EDIT_VIEW);
    }
}
