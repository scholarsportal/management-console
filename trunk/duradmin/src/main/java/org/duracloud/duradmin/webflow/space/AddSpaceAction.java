package org.duracloud.duradmin.webflow.space;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStore.AccessType;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.execution.RequestContext;


public class AddSpaceAction implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(AddSpaceAction.class);
    private transient ContentStoreManager contentStoreManager;

    public ContentStoreManager getContentStoreManager() {
        return contentStoreManager;
    }
    
    public void setContentStoreManager(ContentStoreManager contentStoreManager) {
        this.contentStoreManager = contentStoreManager;
    }
    
    private ContentStore getContentStore() throws ContentStoreException {
        return contentStoreManager.getPrimaryContentStore();
    }
    
    public boolean execute(Space space, MessageContext context) throws Exception{
        try {
            String spaceId = space.getSpaceId();
            ContentStore contentStore = getContentStore();
            contentStore.createSpace(spaceId, null);
            contentStore.setSpaceAccess(spaceId, AccessType.valueOf(space.getAccess()));
            SpaceUtil.populateSpace(space, contentStore.getSpace(spaceId));
            return true;
        } catch (ContentStoreException e) {
            log.error(e);
            context.addMessage(
                               new MessageBuilder().error()
                                   .code("spaceNotAdded").defaultText("Space cannot be added:{0}")
                                   .resolvableArg(e.getMessage())
                                   .build());
            return false;
        }
    }
        
}         