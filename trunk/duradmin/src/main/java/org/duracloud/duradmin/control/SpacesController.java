
package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.duracloud.duradmin.domain.Space;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class SpacesController
        extends BaseFormController {

    protected final Logger log = Logger.getLogger(getClass());

    public SpacesController() {
        setCommandClass(Space.class);
        setCommandName("space");
    }

    @Override
    protected boolean isFormSubmission(HttpServletRequest request) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected ModelAndView onSubmit(Object command, BindException errors)
            throws Exception {
        ModelAndView mav =
                new ModelAndView(getSuccessView(), "spaces", getSpaces());
        return mav;
    }

}