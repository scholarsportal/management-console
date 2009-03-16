package org.duraspace.control;

import java.util.Properties;

import org.apache.log4j.Logger;

import org.duraspace.domain.ContentItem;
import org.duraspace.domain.ContentMetadata;
import org.duraspace.storage.StorageProvider;
import org.duraspace.storage.StorageProviderUtility;
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
    protected ModelAndView onSubmit(Object command,
                                    BindException errors)
    throws Exception {
        ContentItem contentItem = (ContentItem) command;
        String customerId = contentItem.getCustomerId();
        String spaceId = contentItem.getSpaceId();
        String contentId = contentItem.getContentId();

        StorageProvider storage =
            StorageProviderUtility.getStorageProvider(customerId);

        Properties contentProps =
            storage.getContentMetadata(spaceId, contentId);
        ContentMetadata metadata = new ContentMetadata();
        metadata.setName(
            contentProps.getProperty(StorageProvider.METADATA_CONTENT_NAME));
        metadata.setMimetype(
            contentProps.getProperty(StorageProvider.METADATA_CONTENT_MIMETYPE));
        metadata.setSize(
            contentProps.getProperty(StorageProvider.METADATA_CONTENT_SIZE));
        metadata.setChecksum(
            contentProps.getProperty(StorageProvider.METADATA_CONTENT_CHECKSUM));
        metadata.setModified(
            contentProps.getProperty(StorageProvider.METADATA_CONTENT_MODIFIED));
        contentItem.setMetadata(metadata);

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("content", contentItem);

        return mav;
    }

}