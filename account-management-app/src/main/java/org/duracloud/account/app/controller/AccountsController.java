package org.duracloud.account.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(AccountsController.BASE_MAPPING)
public class AccountsController extends AbstractRootController{
    public static final String BASE_MAPPING = RootConsoleHomeController.BASE_MAPPING + "/accounts";
    private static final String BASE_VIEW = BASE_MAPPING;
    
    @RequestMapping("")
    public ModelAndView get() {
        ModelAndView mav = new ModelAndView(BASE_VIEW);
        return mav;
    }

}
