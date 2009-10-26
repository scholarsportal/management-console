package org.duracloud.duradmin.webflow.content;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.domain.Content;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.util.StringUtils;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.web.multipart.MultipartFile;

public class ContentItemValidator {

    private ContentStoreProvider contentStoreProvider;
    
    public ContentStoreProvider getContentStoreProvider() {
        return contentStoreProvider;
    }

    public void setContentStoreProvider(ContentStoreProvider contentStoreProvider) {
        this.contentStoreProvider = contentStoreProvider;
    }

    public void validateDefineContentItem(ContentItem contentItem, ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        MultipartFile file = contentItem.getFile();
        if (file == null || file.isEmpty()) {
            messages.addMessage(new MessageBuilder()
                                    .error()
                                    .source("file")
                                    .code("required").build());
        }
        
        String contentId = contentItem.getContentId();
        if(!StringUtils.isEmptyOrAllWhiteSpace(contentId)){
            //from http://docs.amazonwebservices.com/AmazonS3/2006-03-01/gsg/
            //The key may be any UTF-8 string
            //no validation required for a string > in length;
        }
        
        //how about mimetype validation?
        //TODO Discuss any validation rules for mimetype.
        //check that space doesn't already exist.
        
        //check if item already exists.
        try{
            if(contentItemExists(contentItem)){
                messages.addMessage(new MessageBuilder().error().source("spaceId").
                                    defaultText("A content item with this ID already exists. Please try another.").build());
            }
        }catch(ContentStoreException ex){
            messages.addMessage(new MessageBuilder().error().source("spaceId").
                                defaultText("Unable to validate contentId: " + ex.getMessage()).build());
        }
        
    }
    
    private boolean contentItemExists(ContentItem contentItem) throws ContentStoreException{
        ContentStore contentStore = this.contentStoreProvider.getContentStore();
        Content content = contentStore.getContent(contentItem.getSpaceId(), contentItem.getContentId());
        return (content != null);
    }
}
