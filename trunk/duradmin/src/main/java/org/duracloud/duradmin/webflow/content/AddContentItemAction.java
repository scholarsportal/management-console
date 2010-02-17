
package org.duracloud.duradmin.webflow.content;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.duradmin.util.FileData;
import org.duracloud.duradmin.util.SpaceUtil;
import org.duracloud.error.ContentStoreException;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;

public class AddContentItemAction
        implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static Log log = LogFactory.getLog(AddContentItemAction.class);

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

    public boolean execute(ContentItem contentItem,
                           Space space,
                           MessageContext messageContext) throws Exception {
        try {

            String spaceId = space.getSpaceId();

            ContentStore contentStore = getContentStore();

            String contentId = contentItem.getContentId();

            String contentMimeType = contentItem.getContentMimetype();

            FileData fileData = contentItem.getFileData();
            
            byte[] contents = fileData.getData();
            
            
            
            contentStore.addContent(spaceId,
                                    contentId,
                                    new ByteArrayInputStream(contents),
                                    contents.length,
                                    contentMimeType,
                                    null);
            
            fileData.dereferenceFileData();

            SpaceUtil.populateSpace(space, contentStore.getSpace(spaceId,
                                                                 null,
                                                                 0,
                                                                 null));
            return true;
        } catch (ContentStoreException e) {
            log.error(e);
            messageContext.addMessage(new MessageBuilder().error()
                    .code("transaction.failure").arg(e.getMessage()).build());
            return false;
        }
    }

}