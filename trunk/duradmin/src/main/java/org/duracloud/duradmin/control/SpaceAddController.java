
package org.duracloud.duradmin.control;

import java.util.List;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStore.AccessType;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class SpaceAddController
        extends BaseController {

    protected final Logger log = Logger.getLogger(getClass());

    public SpaceAddController() {
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

        String error = null;
        String spaceId = space.getSpaceId();

        if (spaceId == null || spaceId.equals("")) {
            error = "The Space ID must be non-empty in order to add a space.";
        } else {
            store.createSpace(spaceId, null);
            String access = space.getAccess();
            if (access != null) {
                store.setSpaceAccess(spaceId, AccessType.valueOf(access));
            }
        }

        List<Space> spaces = SpaceUtil.getSpacesList(store.getSpaces());
        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("spaces", spaces);
        if (error != null) {
            mav.addObject("error", error);
        }

        return mav;
    }

}