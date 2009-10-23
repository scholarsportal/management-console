package org.duracloud.duradmin.webflow.space;

import org.duracloud.client.ContentStoreException;
import org.duracloud.duradmin.contentstore.ContentStoreProvider;
import org.duracloud.duradmin.domain.Space;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.util.StringUtils;

public class SpaceValidator {
   
    private ContentStoreProvider contentStoreProvider;
    
    public ContentStoreProvider getContentStoreProvider() {
        return contentStoreProvider;
    }

    public void setContentStoreProvider(ContentStoreProvider contentStoreProvider) {
        this.contentStoreProvider = contentStoreProvider;
    }

    public void validateDefineSpace(Space space, ValidationContext context) {
        MessageContext messages = context.getMessageContext();
        String spaceId = space.getSpaceId();
        if (spaceId == null || !StringUtils.hasLength(spaceId.trim())) {
            messages.addMessage(new MessageBuilder().error().source("spaceId").
                defaultText("Space is required.").build());
        } 

        //from http://docs.amazonwebservices.com/AmazonS3/2006-03-01/gsg/
        //The name can be any string you choose (up to 255 bytes in length), 
        //but cannot be the same as any other bucket name already owned by an 
        //Amazon S3 user. Keep in mind that the bucket name is visible in any 
        //URLs that address your objects. So, you should choose a name that is 
        //appropriate in that context.
        
        
        int len = spaceId.length();

        if(len > 255){
            messages.addMessage(new MessageBuilder().error().source("spaceId").
                                defaultText("Space must be between 3 and 255").build());
        }
        
        //check that space doesn't already exist.
        try{
            if(spaceExists(spaceId)){
                messages.addMessage(new MessageBuilder().error().source("spaceId").
                                    defaultText("A space with this ID already exists. Please try another name.").build());
            }
        }catch(ContentStoreException ex){
            messages.addMessage(new MessageBuilder().error().source("spaceId").
                                defaultText("Unable to validate spaceId: " + ex.getMessage()).build());
        }
        
    }
    
    private boolean spaceExists(String spaceId) throws ContentStoreException{
        return this.contentStoreProvider
                            .getContentStore()
                            .getSpaces()
                            .contains(spaceId);
        
    }
}
