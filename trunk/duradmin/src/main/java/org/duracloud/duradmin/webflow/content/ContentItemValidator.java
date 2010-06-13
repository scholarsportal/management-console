/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.webflow.content;

import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.duracloud.domain.Content;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.storage.util.IdUtil;
import org.duracloud.storage.error.InvalidIdException;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.util.StringUtils;

public class ContentItemValidator {

    private ContentStoreProvider contentStoreProvider;

    public ContentStoreProvider getContentStoreProvider() {
        return contentStoreProvider;
    }

    public void setContentStoreProvider(ContentStoreProvider contentStoreProvider) {
        this.contentStoreProvider = contentStoreProvider;
    }

    public void validateDefineContentItem(ContentItem contentItem,
                                          ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        if (!StringUtils.hasText(contentItem.getFileData()
                .getName())) {
            messages.addMessage(new MessageBuilder().error().source("file")
                    .code("required").build());
        }

        String contentId = contentItem.getContentId();

        if(!StringUtils.hasText(contentId)) {
            messages.addMessage(new MessageBuilder().error()
                                .source("contentId").code("required").build());
        }else{
            try {
                IdUtil.validateContentId(contentId);
                /*
                if (contentItemExists(contentItem)) {
                    messages
                            .addMessage(new MessageBuilder()
                                    .error()
                                    .defaultText("A content item with this ID already exists. Please try another.")
                                    .build());
                }
                */
            
            } catch(InvalidIdException e) {
                messages.addMessage(new MessageBuilder().error()
                    .source("contentId")
                    .defaultText(e.getMessage()).build());
            }
        }
        //how about mimetype validation?
        //TODO Discuss any validation rules for mimetype.
    }

    private boolean contentItemExists(ContentItem contentItem) {
        try {
            ContentStore contentStore =
                    this.contentStoreProvider.getContentStore();
            Content content =
                    contentStore.getContent(contentItem.getSpaceId(),
                                            contentItem.getContentId());
            return (content != null);
        } catch (ContentStoreException e) {
            return false;
        }
    }
}
