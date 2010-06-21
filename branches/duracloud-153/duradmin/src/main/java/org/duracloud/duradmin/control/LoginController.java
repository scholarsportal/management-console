package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @author: Daniel Bernstein
 * Date: March 21, 2010
 */
public class LoginController implements Controller{
    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response)
            throws Exception {
        return new ModelAndView("login");
    }

}
