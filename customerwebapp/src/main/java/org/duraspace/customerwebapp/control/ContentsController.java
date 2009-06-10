package org.duraspace.customerwebapp.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.duraspace.customerwebapp.domain.Space;
import org.duraspace.customerwebapp.util.SpaceUtil;
import org.duraspace.customerwebapp.util.StorageProviderFactory;
import org.duraspace.storage.provider.StorageProvider;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import static org.duraspace.storage.util.StorageProviderUtil.getList;

public class ContentsController extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

	public ContentsController()	{
		setCommandClass(Space.class);
		setCommandName("space");
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
        Space space = (Space) command;
        String spaceId = space.getSpaceId();

        if(spaceId == null || spaceId.equals("")) {
            throw new IllegalArgumentException("Space ID must be provided.");
        }

        StorageProvider storage = StorageProviderFactory.getStorageProvider();

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