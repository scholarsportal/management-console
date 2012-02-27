package org.duracloud.account.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping(ServiceRepositoryController.BASE_MAPPING)
public class ServiceRepositoryController {
    public static final String BASE_MAPPING = RootConsoleHomeController.BASE_MAPPING + "/servicerepositories";
    private static final String NEW_MAPPING = AbstractController.NEW_MAPPING;
    private static final String BASE_VIEW = BASE_MAPPING;
    private static final String NEW_VIEW = BASE_MAPPING + NEW_MAPPING;
    
    @RequestMapping("")
    public ModelAndView redirect() {
        return new ModelAndView(new RedirectView(BASE_MAPPING+"/", true));
    }
 
    
    @RequestMapping("/")
    public ModelAndView get(){
        return new ModelAndView(BASE_VIEW);
    }

    @RequestMapping(NEW_MAPPING)
    public ModelAndView getNew(){
        return new ModelAndView(NEW_VIEW);
    }

    @RequestMapping(value=NEW_MAPPING, method = RequestMethod.POST )
    public ModelAndView postNew(){
        return new ModelAndView(NEW_VIEW);
    }


}
