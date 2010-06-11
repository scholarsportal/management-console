
package org.duracloud.duradmin.control;

import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.util.ControllerUtils;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.SpaceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ContentController
        extends BaseFormController {

    private static final String CONTENT_ITEM = "contentItem";

    protected final Logger log = LoggerFactory.getLogger(ContentController.class);

    public ContentController() {
        setCommandClass(ContentItem.class);
        setCommandName(CONTENT_ITEM);
    }

    @Override
    protected boolean isFormSubmission(HttpServletRequest request) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected ModelAndView onSubmit(Object command, BindException errors)
            throws Exception {
        ContentItem contentItem = (ContentItem) command;
        String spaceId = contentItem.getSpaceId();
        String contentId = contentItem.getContentId();
        ModelAndView mav = new ModelAndView(getSuccessView());

        ControllerUtils.checkContentItemId(spaceId, contentId);

        ContentStore store = null;
        try {
            store = getContentStore();
        } catch (Exception se) {
            mav.setViewName("error");
            mav.addObject("error", se.getMessage());
            return mav;
        }

        Map<String, String> contentMetadata =
                store.getContentMetadata(spaceId, contentId);

        String action = contentItem.getAction();
        if (action != null && action.equals("update")) {
            String newMime = contentItem.getContentMimetype();
            if (StringUtils.hasText(newMime)
                    && !newMime.equals(contentMetadata
                            .get(ContentStore.CONTENT_MIMETYPE))) {
                Map<String, String> updatedMetadata =
                        new HashMap<String, String>();
                updatedMetadata.put(ContentStore.CONTENT_MIMETYPE, newMime);
                store.setContentMetadata(spaceId, contentId, updatedMetadata);
                MessageUtils.addFlashMessage("Successfully modified content.",
                                             mav);
            }
        }

        SpaceUtil.populateContentItem(contentItem,
                                      spaceId,
                                      contentId,
                                      store,
                                      getServicesManager());
        mav.addObject(CONTENT_ITEM, contentItem);
        return mav;
    }

}