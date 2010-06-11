
package org.duracloud.duradmin.control;
import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SpaceDataController
        extends BaseCommandController {

    protected final Logger log = LoggerFactory.getLogger(SpaceDataController.class);

    public SpaceDataController() {
        setCommandClass(Space.class);
        setCommandName("space");
    }

    @Override
    protected void initBinder(HttpServletRequest request,
                              ServletRequestDataBinder binder) throws Exception {
        // TODO Auto-generated method stub
        super.initBinder(request, binder);
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        Space space = (Space) command;
        String spaceId = space.getSpaceId();
        ContentStore store = getContentStore();

        SpaceUtil.populateSpace(space, store.getSpace(spaceId, null, 0, null));

        ModelAndView mav = new ModelAndView();
        mav.setViewName("jsonView");
        mav.getModel().clear();
        mav.addObject(space);
        return mav;
    }

}