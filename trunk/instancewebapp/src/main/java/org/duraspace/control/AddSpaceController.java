package org.duraspace.control;

import java.util.List;

import org.apache.log4j.Logger;

import org.duraspace.domain.Space;
import org.duraspace.storage.StorageProvider;
import org.duraspace.util.SpaceUtil;
import org.duraspace.util.StorageProviderUtil;
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
        String accountId = space.getAccountId();
        String spaceId = space.getSpaceId();

        if(accountId == null || accountId.equals("")) {
            throw new Exception("Account ID must be provided in order to add a space.");
        }

        StorageProvider storage =
            StorageProviderUtil.getStorageProvider(accountId);

        String error = null;
        if(spaceId == null || spaceId.equals("")) {
            error = "The Space ID must be non-empty in order to add a space.";
        } else {
            storage.createSpace(spaceId);
        }

        List<Space> spaces = SpaceUtil.getSpacesList(accountId);

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("spaces", spaces);
        mav.addObject("accountId", accountId);

        if(error != null) {
            mav.addObject("error", error);
        }

        return mav;
    }

}