
package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.Space;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ContentsController
        extends BaseController {

    protected final Logger log = Logger.getLogger(getClass());

    public ContentsController() {
        setCommandClass(Space.class);
        setCommandName("space");
    }
    
    @Override
    protected boolean isFormSubmission(HttpServletRequest request) {
        return true;
    }
    
    @Override
    protected ModelAndView onSubmit(Object command, BindException errors)
            throws Exception {
        Space space = (Space) command;
        String spaceId = space.getSpaceId();
        ModelAndView mav = SpacesHelper.prepareContentsView(spaceId, getContentStore());  
        mav.setViewName(getSuccessView());
        return mav;
    }

}