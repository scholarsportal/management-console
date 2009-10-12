
package org.duracloud.duradmin.control;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

public class AddContentController
        extends BaseFormController {

    protected final Logger log = Logger.getLogger(getClass());

    public AddContentController() {
        setCommandClass(ContentItem.class);
        setCommandName("content");
    }

    @Override
    protected ModelAndView onSubmit(Object command, BindException errors)
            throws Exception {
        ContentItem content = (ContentItem) command;

        String spaceId = content.getSpaceId();
        if (spaceId == null || spaceId.equals("")) {
            throw new IllegalArgumentException("Space ID must be provided.");
        }

        ContentStore store = null;
        try {
            store = getContentStore();
        } catch (Exception se) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", se.getMessage());
            return mav;
        }

        String error = null;
        MultipartFile file = content.getFile();
        if (file == null || file.isEmpty()) {
            error = "A file must be provided in order to add content.";
        } else {
            String contentId = content.getContentId();
            if (contentId == null || contentId.equals("")) {
                contentId = file.getOriginalFilename();
            }

            String contentMime = content.getContentMimetype();
            if (contentMime == null || contentMime.equals("")) {
                contentMime = file.getContentType();
            }

            store.addContent(spaceId, contentId, file.getInputStream(), file
                    .getSize(), contentMime, null);
        }

        // Create a Space for the view
        Space space = new Space();
        space.setSpaceId(spaceId);
        SpaceUtil.populateSpace(space, store.getSpace(spaceId));

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("space", space);

        if (error != null) {
            mav.addObject("error", error);
        }

        return mav;
    }

}