/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;

import org.duracloud.duradmin.domain.Space;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class SpacesController
        extends BaseFormController {

    protected final Logger log = LoggerFactory.getLogger(SpacesController.class);

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