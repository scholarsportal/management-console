package org.duracloud.duradmin.webflow.content;

import junit.framework.Assert;

import org.duracloud.client.ContentStore;
import org.duracloud.duradmin.contentstore.ContentStoreProviderTestBase;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.mock.MockValidationContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.mock.web.MockMultipartFile;


public class ContentItemValidatorTest extends ContentStoreProviderTestBase{
    ContentItemValidator v = new ContentItemValidator();
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        v.setContentStoreProvider(contentStoreProvider);
    }    
   
    
    
    @Test
    public void checkQuestionMark() throws Exception{
        ContentStore cs = this.contentStoreProvider.getContentStore();
        String spaceId = cs.getSpaces().iterator().next();
        ContentItem ci = new ContentItem();
        ci.setSpaceId(spaceId);
        ci.setFile(new MockMultipartFile("my?file.jpg", "my?file.jpg", "image/jpg", "test".getBytes()));
        ValidationContext vc = new MockValidationContext();
        v.validateDefineContentItem(ci, vc);
        Assert.assertEquals(1, vc.getMessageContext().getAllMessages().length);
        
    }
    
    @Test
    public void checkNoFile() throws Exception{
        ContentStore cs = this.contentStoreProvider.getContentStore();
        String spaceId = cs.getSpaces().iterator().next();
        ContentItem ci = new ContentItem();
        ci.setSpaceId(spaceId);
        ci.setContentId("test?");
        ValidationContext vc = new MockValidationContext();
        v.validateDefineContentItem(ci, vc);
        Assert.assertEquals(2, vc.getMessageContext().getAllMessages().length);
        
    }

}
