package org.duracloud.account.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping(RootConsoleHomeController.BASE_MAPPING)
public class RootConsoleHomeController {

    public static final String BASE_MAPPING = "/root";
    private static final String ROOT_HOME_VIEW_ID = "/root";

    @RequestMapping(value={"/", ""})
    public ModelAndView getHome(){
        return new ModelAndView(
            new RedirectView(AccountsController.BASE_MAPPING, true));
    }

}
