
package org.duracloud.duradmin.control;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStore.AccessType;
import org.duracloud.duradmin.contentstore.ContentItemListCache;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.binding.message.Message;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ChangeSpaceAccessController
        extends BaseCommandController {

    protected final Logger log = Logger.getLogger(getClass());

    public ChangeSpaceAccessController() {
        setCommandClass(Space.class);
        setCommandName("space");
    }

    @Override
    protected ModelAndView handle(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Object command,
                                  BindException errors) throws Exception {
        Space space = (Space) command;

        ContentStore store = getContentStore();
        SpaceUtil.populateSpace(space, store.getSpace(space.getSpaceId()));
        AccessType access = AccessType.valueOf(space.getMetadata().getAccess());
        AccessType newAccess =
                access.equals(AccessType.OPEN) ? AccessType.CLOSED
                        : AccessType.OPEN;
        store.setSpaceAccess(space.getSpaceId(), newAccess);
        SpaceUtil.populateSpace(space, store.getSpace(space.getSpaceId()));
        ContentItemListCache.refresh(request, space.getSpaceId(), getContentStoreProvider());
        
        ModelAndView mav = new ModelAndView();
        String text =
                MessageFormat.format("Space access is now {0}", newAccess
                        .toString().toLowerCase());
        Message message = MessageUtils.createMessage(text);
        mav.addObject("space", space);
        return setView(request, mav, message);
    }

}