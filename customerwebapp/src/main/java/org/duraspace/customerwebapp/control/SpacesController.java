package org.duraspace.customerwebapp.control;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import org.duraspace.customerwebapp.domain.Space;
import org.duraspace.customerwebapp.util.SpaceUtil;
import org.duraspace.customerwebapp.util.StorageProviderFactory;
import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.provider.StorageProvider;
import org.duraspace.storage.provider.StorageProvider.AccessType;
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
        String accountId = space.getAccountId();

        if(accountId == null || accountId.equals("")) {
            throw new IllegalArgumentException("Account ID must be provided.");
        }

        StorageProvider storage = null;
        try {
            storage = StorageProviderFactory.getStorageProvider(accountId);
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
                    String name = space.getName();
                    if(name != null) {
                        Map<String, String> spaceProperties = storage.getSpaceMetadata(spaceId);
                        spaceProperties.put(StorageProvider.METADATA_SPACE_NAME, name);
                        storage.setSpaceMetadata(spaceId, spaceProperties);
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
            // Update Space Name
            } else if(action.equals("update-name")) {
                String newName = space.getName();
                Map<String, String> spaceProperties = storage.getSpaceMetadata(spaceId);
                String oldName = spaceProperties.get(StorageProvider.METADATA_SPACE_NAME);
                if(newName != null && !newName.equals(oldName)) {
                  spaceProperties.put(StorageProvider.METADATA_SPACE_NAME, newName);
                  storage.setSpaceMetadata(spaceId, spaceProperties);
                }
            }
        }

        List<Space> spaces = SpaceUtil.getSpacesList(accountId);

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("accountId", accountId);
        mav.addObject("spaces", spaces);

        if(error != null) {
            mav.addObject("error", error);
        }

        return mav;
    }

}