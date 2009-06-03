package org.duraspace.customerwebapp.control;

import java.util.List;

import org.apache.log4j.Logger;

import org.duraspace.customerwebapp.domain.ContentItem;
import org.duraspace.customerwebapp.domain.Space;
import org.duraspace.customerwebapp.util.SpaceUtil;
import org.duraspace.customerwebapp.util.StorageProviderFactory;
import org.duraspace.storage.provider.StorageProvider;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import static org.duraspace.storage.util.StorageProviderUtil.getList;

public class RemoveContentController extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

	public RemoveContentController()	{
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
        String contentId = content.getContentId();

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

        storage.deleteContent(spaceId, contentId);

        // Create a Space for the view
        Space space = new Space();
        space.setAccountId(accountId);
        space.setSpaceId(spaceId);

        // Get the metadata of the space
        space.setMetadata(SpaceUtil.getSpaceMetadata(storage, spaceId));

        // Get the list of items in the space
        List<String> contents = getList(storage.getSpaceContents(spaceId));
        space.setContents(contents);

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("space", space);

        return mav;
    }

}