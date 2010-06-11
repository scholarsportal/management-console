
package org.duracloud.duradmin.control;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStore.AccessType;
import org.duracloud.duradmin.contentstore.ContentItemListCache;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.MessageUtils;
import org.duracloud.duradmin.util.SpaceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.Message;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

public class ChangeSpaceAccessController
        extends BaseCommandController {

    protected final Logger log = LoggerFactory.getLogger(ChangeSpaceAccessController.class);

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
        SpaceUtil.populateSpace(space, store.getSpace(space.getSpaceId(), null, 0, null));
        AccessType access = AccessType.valueOf(space.getMetadata().getAccess());
        AccessType newAccess =
                access.equals(AccessType.OPEN) ? AccessType.CLOSED
                        : AccessType.OPEN;
        store.setSpaceAccess(space.getSpaceId(), newAccess);
        SpaceUtil.populateSpace(space, store.getSpace(space.getSpaceId(), null, 0, null));
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