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
            messages.addMessage(new MessageBuilder().error().source("spaceId")
                                                    .code("required").build());
        } 

        //from http://docs.amazonwebservices.com/AmazonS3/2006-03-01/gsg/
        //The name can be any string you choose (up to 63 bytes in length), 
        //but cannot be the same as any other bucket name already owned by an 
        //Amazon S3 user. Keep in mind that the bucket name is visible in any 
        //URLs that address your objects. So, you should choose a name that is 
        //appropriate in that context.
        
        
        int len = spaceId.length();

        if(len < 3 || len > 63 ||  !spaceId.matches("^[a-z0-9]([a-z0-9]|[-.](?![-.]))*([^-])$")){
            messages.addMessage(new MessageBuilder().error().source("spaceId").
                                defaultText("A space id must satisfy the following conditions: " +
                                		"1)be at least 3 and less than 64 characters in length," +
                                		"2) contain only lowercase letters, numbers, periods or dashes; " +
                                		"3) start with a number or letter;" +
                                		"4) contain no adjacent periods or dashes;" +
                                		"5) may not end with a dash.").build());
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
