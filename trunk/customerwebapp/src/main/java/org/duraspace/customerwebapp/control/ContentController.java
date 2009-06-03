package org.duraspace.customerwebapp.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.duraspace.customerwebapp.domain.ContentItem;
import org.duraspace.customerwebapp.domain.ContentMetadata;
import org.duraspace.customerwebapp.util.StorageProviderFactory;
import org.duraspace.storage.provider.StorageProvider;
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
        String accountId = contentItem.getAccountId();
        String spaceId = contentItem.getSpaceId();
        String contentId = contentItem.getContentId();

        if(accountId == null || accountId.equals("")) {
            throw new IllegalArgumentException("Account ID must be provided.");
        }
        if(spaceId == null || spaceId.equals("")) {
            throw new IllegalArgumentException("Space ID must be provided.");
        }
        if(contentId == null || contentId.equals("")) {
            throw new IllegalArgumentException("Content ID must be provided.");
        }

        StorageProvider storage =
            StorageProviderFactory.getStorageProvider(accountId);

        Map<String, String> contentMetadata =
            storage.getContentMetadata(spaceId, contentId);

        String action = contentItem.getAction();
        if(action != null && action.equals("update")) {
            String newName = contentItem.getContentName();
            String newMime = contentItem.getContentMimetype();
            if(newName != null && newMime != null) {
                contentMetadata.
                  put(StorageProvider.METADATA_CONTENT_NAME, newName);
                contentMetadata.
                  put(StorageProvider.METADATA_CONTENT_MIMETYPE, newMime);
                storage.setContentMetadata(spaceId, contentId, contentMetadata);
            }
        }

        ContentMetadata metadata = new ContentMetadata();
        metadata.setName(
            contentMetadata.get(StorageProvider.METADATA_CONTENT_NAME));
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