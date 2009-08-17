package org.duracloud.customerwebapp.control;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.duracloud.customerwebapp.domain.ContentItem;
import org.duracloud.customerwebapp.domain.ContentMetadata;
import org.duracloud.customerwebapp.util.StorageProviderFactory;
import org.duracloud.storage.provider.StorageProvider;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ContentController extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

	public ContentController()
	{
		setCommandClass(ContentItem.class);
		setCommandName("content");
	}

    @Override
    protected boolean isFormSubmission(HttpServletRequest request){
        // Process both GET and POST requests as form submissions
        return true;
    }

    @Override
    protected ModelAndView onSubmit(Object command,
                                    BindException errors)
    throws Exception {
        ContentItem contentItem = (ContentItem) command;
        String spaceId = contentItem.getSpaceId();
        String contentId = contentItem.getContentId();

        if(spaceId == null || spaceId.equals("")) {
            throw new IllegalArgumentException("Space ID must be provided.");
        }
        if(contentId == null || contentId.equals("")) {
            throw new IllegalArgumentException("Content ID must be provided.");
        }

        StorageProvider storage = StorageProviderFactory.getStorageProvider();

        Map<String, String> contentMetadata = null;
        String action = contentItem.getAction();
        if(action != null && action.equals("update")) {
            String newMime = contentItem.getContentMimetype();
            if(newMime != null) {
                contentMetadata = new HashMap<String, String>();
                contentMetadata.
                  put(StorageProvider.METADATA_CONTENT_MIMETYPE, newMime);
                storage.setContentMetadata(spaceId, contentId, contentMetadata);
            }
        }

        contentMetadata = storage.getContentMetadata(spaceId, contentId);
        ContentMetadata metadata = new ContentMetadata();
        metadata.setMimetype(
            contentMetadata.get(StorageProvider.METADATA_CONTENT_MIMETYPE));
        metadata.setSize(
            contentMetadata.get(StorageProvider.METADATA_CONTENT_SIZE));
        metadata.setChecksum(
            contentMetadata.get(StorageProvider.METADATA_CONTENT_CHECKSUM));
        metadata.setModified(
            contentMetadata.get(StorageProvider.METADATA_CONTENT_MODIFIED));
        contentItem.setMetadata(metadata);

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("content", contentItem);

        return mav;
    }

}