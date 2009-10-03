
package org.duracloud.duradmin.control;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.ContentMetadata;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ContentController
        extends BaseController {

    protected final Logger log = Logger.getLogger(getClass());

    public ContentController() {
        setCommandClass(ContentItem.class);
        setCommandName("content");
    }

    @Override
    protected ModelAndView onSubmit(Object command, BindException errors)
            throws Exception {
        ContentItem contentItem = (ContentItem) command;
        String spaceId = contentItem.getSpaceId();
        String contentId = contentItem.getContentId();

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

        Map<String, String> contentMetadata = null;
        String action = contentItem.getAction();
        if (action != null && action.equals("update")) {
            String newMime = contentItem.getContentMimetype();
            if (newMime != null) {
                contentMetadata = new HashMap<String, String>();
                contentMetadata.put(ContentStore.CONTENT_MIMETYPE, newMime);
                store.setContentMetadata(spaceId, contentId, contentMetadata);
            }
        }

        contentMetadata = store.getContentMetadata(spaceId, contentId);
        ContentMetadata metadata = new ContentMetadata();
        metadata
                .setMimetype(contentMetadata.get(ContentStore.CONTENT_MIMETYPE));
        metadata.setSize(contentMetadata.get(ContentStore.CONTENT_SIZE));
        metadata
                .setChecksum(contentMetadata.get(ContentStore.CONTENT_CHECKSUM));
        metadata
                .setModified(contentMetadata.get(ContentStore.CONTENT_MODIFIED));
        contentItem.setMetadata(metadata);

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("content", contentItem);

        return mav;
    }

}