package org.duraspace.control;

import java.util.List;

import org.apache.log4j.Logger;

import org.duraspace.domain.Space;
import org.duraspace.domain.SpaceUtil;
import org.duraspace.storage.StorageProvider;
import org.duraspace.storage.StorageProviderUtility;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class AddSpaceController extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

	public AddSpaceController()	{
		setCommandClass(Space.class);
		setCommandName("space");
	}

    @Override
    protected ModelAndView onSubmit(Object command,
                                    BindException errors)
    throws Exception {
        Space space = (Space) command;
        String customerId = space.getCustomerId();
        String spaceId = space.getSpaceId();

        StorageProvider storage =
            StorageProviderUtility.getStorageProvider(customerId);

        storage.createSpace(spaceId);
        List<Space> spaces = SpaceUtil.getSpacesList(customerId);

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("spaces", spaces);

        return mav;
    }

}