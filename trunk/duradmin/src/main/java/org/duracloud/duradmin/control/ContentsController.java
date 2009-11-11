
package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.duracloud.duradmin.domain.Space;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ContentsController
        extends BaseCommandController {

    protected final Logger log = Logger.getLogger(getClass());
    
    private String successView;

    public ContentsController() {
        setCommandClass(Space.class);
        setCommandName("space");
    }
    
    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        Space space = (Space) command;
        String spaceId = space.getSpaceId();
        ModelAndView mav = SpacesHelper.prepareContentsView(request, spaceId, getContentStore());  
        mav.setViewName(getSuccessView());
        return mav;
    }

    
    public String getSuccessView() {
        return successView;
    }

    
    public void setSuccessView(String successView) {
        this.successView = successView;
    }    
    

}