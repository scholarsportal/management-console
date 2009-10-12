package org.duracloud.duradmin.webflow.content;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.SpaceUtil;
import org.duracloud.duradmin.util.StringUtils;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.web.multipart.MultipartFile;


public class AddContentItemAction implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog(AddContentItemAction.class);
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
    
    public boolean execute(ContentItem contentItem, Space space, MessageContext messageContext) throws Exception{
        try {

            String spaceId = space.getSpaceId();
            ContentStore contentStore = getContentStore();
            MultipartFile file = contentItem.getFile();

            String contentId = contentItem.getContentId();
            if (StringUtils.isEmptyOrAllWhiteSpace(contentId)) {
                contentId = file.getOriginalFilename();
                contentItem.setContentId(contentId);
            }
            
            String contentMimeType = contentItem.getContentMimetype();
            if (StringUtils.isEmptyOrAllWhiteSpace(contentMimeType)) {
                contentMimeType = file.getContentType();
            }
            contentStore.addContent(spaceId, contentId, file.getInputStream(), 
                                    file.getSize(), contentMimeType, null);
            SpaceUtil.populateSpace(space, contentStore.getSpace(spaceId));

            messageContext.addMessage(new MessageBuilder()
                                          .info()
                                          .code("contentItemAdded")
                                          .defaultText("Content added!").build());
            return true;
        } catch (ContentStoreException e) {
            log.error(e);
            messageContext.addMessage(
                               new MessageBuilder().error()
                                   .code("contentNotAdded").defaultText("Content could not be added:{0}")
                                   .resolvableArg(e.getMessage())
                                   .build());
            return false;
        }
    }
        
}         