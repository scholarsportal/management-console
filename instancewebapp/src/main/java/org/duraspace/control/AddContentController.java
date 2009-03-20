package org.duraspace.control;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import org.duraspace.domain.ContentItem;
import org.duraspace.domain.Space;
import org.duraspace.storage.StorageProvider;
import org.duraspace.util.SpaceUtil;
import org.duraspace.util.StorageProviderUtil;
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

        MultipartFile file = content.getFile();
        if(file == null) {
            throw new IllegalArgumentException("A file must be provided.");
        }

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

        StorageProvider storage =
            StorageProviderUtil.getStorageProvider(accountId);

        storage.addContent(spaceId,
                           contentId,
                           contentMime,
                           file.getSize(),
                           file.getInputStream());

        Properties contentProps = storage.getContentMetadata(spaceId, contentId);
        contentProps.setProperty(StorageProvider.METADATA_CONTENT_NAME, contentName);
        storage.setContentMetadata(spaceId, contentId, contentProps);

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

        return mav;
    }

}