package org.duracloud.customerwebapp.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.duracloud.customerwebapp.domain.Space;
import org.duracloud.customerwebapp.util.SpaceUtil;
import org.duracloud.customerwebapp.util.StorageProviderFactory;
import org.duracloud.storage.domain.StorageException;
import org.duracloud.storage.provider.StorageProvider;
import org.duracloud.storage.provider.StorageProvider.AccessType;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class SpacesController extends SimpleFormController {

    protected final Logger log = Logger.getLogger(getClass());

	public SpacesController()
	{
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

        StorageProvider storage = null;
        try {
            storage = StorageProviderFactory.getStorageProvider();
        } catch(StorageException se) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", se.getMessage());
            return mav;
        }

        String error = null;
        String action = space.getAction();
        if(action != null) {
            String spaceId = space.getSpaceId();
            if(spaceId == null || spaceId.equals("")) {
                // Allow add actions to display a nicer error
                if(!action.equals("add")) {
                    throw new IllegalArgumentException("Space ID must be provided.");
                }
            }

            // Add Space
            if(action.equals("add")) {
                if(spaceId == null || spaceId.equals("")) {
                    error = "The Space ID must be non-empty in order to add a space.";
                } else {
                    storage.createSpace(spaceId);
                    String access = space.getAccess();
                    if(access != null){
                        if(access.equals("OPEN")) {
                            storage.setSpaceAccess(spaceId, AccessType.OPEN);
                        } else if(access.equals("CLOSED")) {
                            storage.setSpaceAccess(spaceId, AccessType.CLOSED);
                        }
                    }
                }
            // Delete Space
            } else if(action.equals("delete")) {
                storage.deleteSpace(spaceId);
            // Update Space Access Setting
            } else if(action.equals("update-access")) {
                String newAccess = space.getAccess();
                if(newAccess != null){
                    AccessType oldAccess = storage.getSpaceAccess(spaceId);
                    if(newAccess.equals("CLOSED") &&
                       oldAccess.equals(AccessType.OPEN)) {
                        storage.setSpaceAccess(spaceId, AccessType.CLOSED);
                    } else if(newAccess.equals("OPEN") &&
                              oldAccess.equals(AccessType.CLOSED)) {
                        storage.setSpaceAccess(spaceId, AccessType.OPEN);
                    }
                }
            }
        }

        List<Space> spaces = SpaceUtil.getSpacesList();

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("spaces", spaces);

        if(error != null) {
            mav.addObject("error", error);
        }

        return mav;
    }

}