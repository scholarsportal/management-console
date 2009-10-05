
package org.duracloud.duradmin.control;

import java.util.List;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class SpaceDeleteController
        extends BaseController {

    protected final Logger log = Logger.getLogger(getClass());

    public SpaceDeleteController() {
        setCommandClass(Space.class);
        setCommandName("space");
    }

    @Override
    protected ModelAndView onSubmit(Object command, BindException errors)
            throws Exception {
        Space space = (Space) command;

        ContentStore store = null;
        try {
            store = getContentStore();
        } catch (Exception se) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", se.getMessage());
            return mav;
        }
        
        store.deleteSpace(space.getSpaceId());
        
        List<Space> spaces = SpaceUtil.getSpacesList(store.getSpaces());
        ModelAndView mav = new ModelAndView(getSuccessView());
        
        mav.addObject("spaces", spaces);
        return mav;
    }

}