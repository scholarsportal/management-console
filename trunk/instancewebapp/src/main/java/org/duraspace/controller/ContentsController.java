package org.duraspace.controller;

import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.duraspace.domain.Space;
import org.duraspace.domain.SpaceMetadata;
import org.duraspace.storage.StorageProvider;
import org.duraspace.storage.StorageProviderUtility;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class ContentsController extends AbstractCommandController {

    protected final Logger log = Logger.getLogger(getClass());

	public ContentsController()
	{
		setCommandClass(Space.class);
		setCommandName("space");
	}

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        Space space = (Space) command;
        String customerId = space.getCustomerId();
        String spaceId = space.getSpaceId();

        StorageProvider storage =
            StorageProviderUtility.getStorageProvider(customerId);

        // Get the name of the space
        Properties spaceProps = storage.getSpaceMetadata(spaceId);
        SpaceMetadata spaceMetadata = new SpaceMetadata();
        String spaceName =
            spaceProps.getProperty(StorageProvider.METADATA_SPACE_NAME);
        if(spaceName == null || spaceName.equals("")) {
            spaceName = spaceId;
        }
        spaceMetadata.setName(spaceName);
        space.setMetadata(spaceMetadata);

        // Get the list of items in the space
        List<String> contents = storage.getSpaceContents(spaceId);
        space.setContents(contents);

        ModelAndView mav = new ModelAndView("contents");
        mav.addObject("space", space);

        return mav;
    }

}