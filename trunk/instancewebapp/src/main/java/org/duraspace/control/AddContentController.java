package org.duraspace.control;

import java.util.List;

import org.apache.log4j.Logger;

import org.duraspace.domain.ContentItem;
import org.duraspace.domain.Space;
import org.duraspace.storage.StorageProvider;
import org.duraspace.util.SpaceUtil;
import org.duraspace.util.StorageProviderUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class AddContentController extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

	public AddContentController()	{
        setCommandClass(ContentItem.class);
        setCommandName("content");
	}

    @Override
    protected ModelAndView onSubmit(Object command,
                                    BindException errors)
    throws Exception {
        ContentItem content = (ContentItem) command;
        String accountId = content.getAccountId();
        String spaceId = content.getSpaceId();

        if(accountId == null || accountId.equals("")) {
            throw new IllegalArgumentException("Account ID must be provided.");
        }
        if(spaceId == null || spaceId.equals("")) {
            throw new IllegalArgumentException("Space ID must be provided.");
        }

        String contentId = content.getContentId();
        String contentName = content.getContentName();
        String contentMime = content.getContentMimetype();

        String error = null;
        if(contentId == null || contentId.equals("")){
            error = "Content ID must be provided to create a new content item.";
        } else {
            // TODO: Add handler code for file upload here
            error = "Adding content (with ID '" + contentId +
                    "' name '" + contentName +
                    "' and mimetype '" + contentMime +
                    "') is not yet implemented.";
        }

        StorageProvider storage =
            StorageProviderUtil.getStorageProvider(accountId);

        // Create a Space for the view
        Space space = new Space();
        space.setAccountId(accountId);
        space.setSpaceId(spaceId);

        // Get the metadata of the space
        space.setMetadata(SpaceUtil.getSpaceMetadata(storage, spaceId));

        // Get the list of items in the space
        List<String> contents = storage.getSpaceContents(spaceId);
        space.setContents(contents);

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("space", space);

        if(error != null) {
            mav.addObject("error", error);
        }

        return mav;
    }

}