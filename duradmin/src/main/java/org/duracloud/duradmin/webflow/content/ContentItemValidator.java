
package org.duracloud.duradmin.webflow.content;

import org.duracloud.client.ContentStore;
import org.duracloud.error.ContentStoreException;
import org.duracloud.domain.Content;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.domain.ContentItem;
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
            //from http://docs.amazonwebservices.com/AmazonS3/2006-03-01/gsg/
            //The key may be any UTF-8 string
            //no validation required for a string > in length;
            //
            //right now spaces are breaking durastore - therefore 
            //I'm adding in the whitespace checker
            if (contentId.matches("^.*[?].*$")
                    || contentId.getBytes().length > 1024) {
                messages.addMessage(new MessageBuilder().error()
                        .source("contentId").code("contentId.invalid").build());
            }
        }
        //how about mimetype validation?
        //TODO Discuss any validation rules for mimetype.

        //check if item already exists.
        if (contentItemExists(contentItem)) {
            messages
                    .addMessage(new MessageBuilder()
                            .error()
                            .defaultText("A content item with this ID already exists. Please try another.")
                            .build());
        }
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
