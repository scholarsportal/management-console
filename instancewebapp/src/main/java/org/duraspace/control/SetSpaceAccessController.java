package org.duraspace.control;

import java.util.List;

import org.apache.log4j.Logger;

import org.duraspace.domain.Space;
import org.duraspace.storage.StorageProvider;
import org.duraspace.storage.StorageProvider.AccessType;
import org.duraspace.util.SpaceUtil;
import org.duraspace.util.StorageProviderUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class SetSpaceAccessController extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

	public SetSpaceAccessController()	{
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
            throw new Exception("Account ID must be provided in order to set space access.");
        }

        if(spaceId == null || spaceId.equals("")) {
            throw new Exception("Space ID must be provided in order to set space access.");
        }

        StorageProvider storage =
            StorageProviderUtil.getStorageProvider(accountId);

        AccessType access = storage.getSpaceAccess(spaceId);
        if(access.equals(AccessType.OPEN)) {
            storage.setSpaceAccess(spaceId, AccessType.CLOSED);
        } else if(access.equals(AccessType.CLOSED)) {
            storage.setSpaceAccess(spaceId, AccessType.OPEN);
        }

        List<Space> spaces = SpaceUtil.getSpacesList(accountId);

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("spaces", spaces);
        mav.addObject("accountId", accountId);

        return mav;
    }

}