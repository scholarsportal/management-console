
package org.duracloud.duradmin.webflow.space;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStore.AccessType;
import org.duracloud.error.ContentStoreException;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;

import java.io.Serializable;

public class AddSpaceAction
        implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static Log log = LogFactory.getLog(AddSpaceAction.class);

    private transient ContentStoreProvider contentStoreProvider;

    public ContentStoreProvider getContentStoreProvider() {
        return contentStoreProvider;
    }

    public void setContentStoreProvider(ContentStoreProvider contentStoreProvider) {
        this.contentStoreProvider = contentStoreProvider;
    }

    private ContentStore getContentStore() throws ContentStoreException {
        return contentStoreProvider.getContentStore();
    }

    public boolean execute(Space space, MessageContext context)
            throws Exception {
        try {
            String spaceId = space.getSpaceId();
            ContentStore contentStore = getContentStore();
            contentStore.createSpace(spaceId, null);
            contentStore.setSpaceAccess(spaceId, AccessType.valueOf(space
                    .getAccess()));
            SpaceUtil.populateSpace(space, contentStore.getSpace(spaceId,
                                                                 null,
                                                                 0,
                                                                 null));
            return true;
        } catch (ContentStoreException e) {
            log.error(e);
            context.addMessage(new MessageBuilder().error()
                    .code("spaceNotAdded")
                    .defaultText("Space cannot be added:{0}").resolvableArg(e
                            .getMessage()).build());
            return false;
        }
    }

}