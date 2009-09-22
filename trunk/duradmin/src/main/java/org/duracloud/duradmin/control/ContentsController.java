package org.duracloud.duradmin.control;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ContentsController extends BaseController {

    protected final Logger log = Logger.getLogger(getClass());

	public ContentsController()	{
		setCommandClass(Space.class);
		setCommandName("space");
	}

    @Override
    protected ModelAndView onSubmit(Object command,
                                    BindException errors)
    throws Exception {
        Space space = (Space) command;
        String spaceId = space.getSpaceId();

        if(spaceId == null || spaceId.equals("")) {
            throw new IllegalArgumentException("Space ID must be provided.");
        }

        ContentStore store = null;
        try {
            store = getContentStore();
        } catch(Exception se) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", se.getMessage());
            return mav;
        }

        SpaceUtil.populateSpace(space, store.getSpace(spaceId));

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("baseURL", store.getBaseURL());
        mav.addObject("space", space);

        return mav;
    }

}