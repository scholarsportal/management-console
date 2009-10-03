
package org.duracloud.duradmin.control;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class RemoveContentController
        extends BaseController {

    protected final Logger log = Logger.getLogger(getClass());

    public RemoveContentController() {
        setCommandClass(ContentItem.class);
        setCommandName("content");
    }

    @Override
    protected ModelAndView onSubmit(Object command, BindException errors)
            throws Exception {
        ContentItem content = (ContentItem) command;
        String spaceId = content.getSpaceId();
        String contentId = content.getContentId();

        if (spaceId == null || spaceId.equals("")) {
            throw new IllegalArgumentException("Space ID must be provided.");
        }
        if (contentId == null || contentId.equals("")) {
            throw new IllegalArgumentException("Content ID must be provided.");
        }

        ContentStore store = null;
        try {
            store = getContentStore();
        } catch (Exception se) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", se.getMessage());
            return mav;
        }

        store.deleteContent(spaceId, contentId);

        // Create a Space for the view
        Space space = new Space();
        space.setSpaceId(spaceId);
        SpaceUtil.populateSpace(space, store.getSpace(spaceId));

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("space", space);

        return mav;
    }

}