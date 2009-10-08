
package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStore.AccessType;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.view.FlashMessage;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class AddSpaceController
        extends BaseController {

    protected final Logger log = Logger.getLogger(getClass());

    public AddSpaceController() {
        setCommandClass(Space.class);
        setCommandName("space");
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest request,
                                    HttpServletResponse response,
                                    BindException errors) throws Exception {
        ModelAndView mav = super.showForm(request, response, errors);
        mav.setViewName("add.space");
        mav.addObject("title", "Add Space");
        return mav;
    }
    
    
    @Override
    protected ModelAndView onSubmit(Object command, BindException errors)
            throws Exception {
        Space space = (Space) command;
        String spaceId = space.getSpaceId();
        ContentStore store = getContentStore();
        store.createSpace(spaceId, null);
        String access = space.getAccess();
        store.setSpaceAccess(spaceId, AccessType.valueOf(access));
        ModelAndView mav = SpacesHelper.prepareContentsView(spaceId, store);  
        mav.setViewName(getSuccessView());
        mav.addObject("flashMessage", new FlashMessage("Successfully added a new space!"));
        return mav;
    }

}