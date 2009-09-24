package org.duracloud.duradmin.control;

import java.util.List;

import org.apache.log4j.Logger;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStore.AccessType;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
import org.duracloud.duradmin.view.MainMenu;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class SpacesController extends BaseController {

    protected final Logger log = Logger.getLogger(getClass());

	public SpacesController()
	{
        setCommandClass(Space.class);
        setCommandName("space");
	}

    @Override
    protected ModelAndView onSubmit(Object command,
                                    BindException errors)
    throws Exception {
        Space space = (Space) command;

        ContentStore store = null;
        try {
            store = getContentStore();
        } catch(Exception se) {
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
                    store.createSpace(spaceId, null);
                    String access = space.getAccess();
                    if(access != null){
                        if(access.equals("OPEN")) {
                            store.setSpaceAccess(spaceId, AccessType.OPEN);
                        } else if(access.equals("CLOSED")) {
                            store.setSpaceAccess(spaceId, AccessType.CLOSED);
                        }
                    }
                }
            // Delete Space
            } else if(action.equals("delete")) {
                store.deleteSpace(spaceId);
            // Update Space Access Setting
            } else if(action.equals("update-access")) {
                String newAccess = space.getAccess();
                if(newAccess != null){
                    AccessType oldAccess = store.getSpaceAccess(spaceId);
                    if(newAccess.equals("CLOSED") &&
                       oldAccess.equals(AccessType.OPEN)) {
                        store.setSpaceAccess(spaceId, AccessType.CLOSED);
                    } else if(newAccess.equals("OPEN") &&
                              oldAccess.equals(AccessType.CLOSED)) {
                        store.setSpaceAccess(spaceId, AccessType.OPEN);
                    }
                }
            }
        }

        List<Space> spaces = SpaceUtil.getSpacesList(store.getSpaces());

        ModelAndView mav = new ModelAndView(getSuccessView());
        mav.addObject("spaces", spaces);

        if(error != null) {
            mav.addObject("error", error);
        }

        return mav;
    }

}