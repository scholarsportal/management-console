package org.duracloud.customerwebapp.control;

import java.util.List;

import org.apache.log4j.Logger;

import org.duracloud.customerwebapp.domain.ContentItem;
import org.duracloud.customerwebapp.domain.Space;
import org.duracloud.customerwebapp.util.SpaceUtil;
import org.duracloud.customerwebapp.util.StorageProviderFactory;
import org.duracloud.storage.provider.StorageProvider;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import static org.duracloud.storage.util.StorageProviderUtil.getList;

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
        String spaceId = content.getSpaceId();
        String contentId = content.getContentId();

        if(spaceId == null || spaceId.equals("")) {
            throw new IllegalArgumentException("Space ID must be provided.");
        }
        if(contentId == null || contentId.equals("")) {
            throw new IllegalArgumentException("Content ID must be provided.");
        }

        StorageProvider storage = StorageProviderFactory.getStorageProvider();

        storage.deleteContent(spaceId, contentId);

        // Create a Space for the view
        Space space = new Space();
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