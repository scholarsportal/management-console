
package org.duracloud.duradmin.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.MessageUtils;
import org.springframework.binding.message.Message;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class RemoveSpaceController
        extends BaseCommandController {

    protected final Logger log = Logger.getLogger(getClass());

    public RemoveSpaceController() {
        setCommandClass(Space.class);
        setCommandName("space");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        Space space = (Space) command;

        ContentStore store = null;
        try {
            store = getContentStore();
        } catch (Exception se) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("error", se.getMessage());
            return mav;
        }
        store.deleteSpace(space.getSpaceId());
        ModelAndView mav = new ModelAndView();
        Message message =
                MessageUtils.createMessage("Successfully removed space");
        return setView(request, mav, message);
    }

}