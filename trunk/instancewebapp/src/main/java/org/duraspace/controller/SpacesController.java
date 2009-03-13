package org.duraspace.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.duraspace.domain.Account;
import org.duraspace.domain.Space;
import org.duraspace.domain.SpaceMetadata;
import org.duraspace.storage.StorageProvider;
import org.duraspace.storage.StorageProviderUtility;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class SpacesController extends AbstractCommandController {

    protected final Logger log = Logger.getLogger(getClass());

	public SpacesController()
	{
		setCommandClass(Account.class);
		setCommandName("account");
	}

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        Account account = (Account) command;
        String customerId = account.getCustomerId();

        StorageProvider storage =
            StorageProviderUtility.getStorageProvider(customerId);

        List<String> spaceIds = storage.getSpaces();

        List<Space> spaces = new ArrayList<Space>();
        if(spaceIds != null && spaceIds.size() > 0) {
            Iterator<String> spaceIdIterator = spaceIds.iterator();
            while(spaceIdIterator.hasNext()) {
                String spaceId = spaceIdIterator.next();
                Space space = new Space();
                space.setCustomerId(customerId);
                space.setSpaceId(spaceId);

                Properties spaceProps = storage.getSpaceMetadata(spaceId);
                SpaceMetadata spaceMetadata = new SpaceMetadata();
                spaceMetadata.setName(
                    spaceProps.getProperty(StorageProvider.METADATA_SPACE_NAME));
                spaceMetadata.setCreated(
                    spaceProps.getProperty(StorageProvider.METADATA_SPACE_CREATED));
                spaceMetadata.setCount(
                    spaceProps.getProperty(StorageProvider.METADATA_SPACE_COUNT));
                spaceMetadata.setAccess(
                    spaceProps.getProperty(StorageProvider.METADATA_SPACE_ACCESS));
                space.setMetadata(spaceMetadata);

                spaces.add(space);
            }
        }

        ModelAndView mav = new ModelAndView("spaces");
        mav.addObject("spaces", spaces);

        return mav;
    }

}