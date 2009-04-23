package org.duraspace.customerwebapp.control;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import org.duraspace.customerwebapp.domain.ContentItem;
import org.duraspace.customerwebapp.domain.Space;
import org.duraspace.customerwebapp.util.SpaceUtil;
import org.duraspace.customerwebapp.util.StorageProviderFactory;
import org.duraspace.storage.provider.StorageProvider;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;
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
        if(accountId == null || accountId.equals("")) {
            throw new IllegalArgumentException("Account ID must be provided.");
        }

        String spaceId = content.getSpaceId();
        if(spaceId == null || spaceId.equals("")) {
            throw new IllegalArgumentException("Space ID must be provided.");
        }

        StorageProvider storage =
            StorageProviderFactory.getStorageProvider(accountId);

        String error = null;
        MultipartFile file = content.getFile();
        if(file == null || file.isEmpty()) {
            error = "A file must be provided in order to add content.";
        } else {
            String contentId = content.getContentId();
            if(contentId == null || contentId.equals("")){
                contentId = file.getOriginalFilename();
            }

            String contentName = content.getContentName();
            if(contentName == null || contentName.equals("")){
                contentName = file.getOriginalFilename();
            }

            String contentMime = content.getContentMimetype();
            if(contentMime == null || contentMime.equals("")) {
                contentMime = file.getContentType();
            }

            storage.addContent(spaceId,
                               contentId,
                               contentMime,
                               file.getSize(),
                               file.getInputStream());

            Properties contentProps = storage.getContentMetadata(spaceId, contentId);
            contentProps.setProperty(StorageProvider.METADATA_CONTENT_NAME, contentName);
            storage.setContentMetadata(spaceId, contentId, contentProps);
        }

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