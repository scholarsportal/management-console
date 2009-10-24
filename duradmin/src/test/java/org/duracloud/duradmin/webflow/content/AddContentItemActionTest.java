package org.duracloud.duradmin.webflow.content;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.duracloud.duradmin.contentstore.BaseContentStoreProviderTest;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.Space;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageCriteria;
import org.springframework.binding.message.MessageResolver;
import org.springframework.web.multipart.MultipartFile;


public class AddContentItemActionTest extends BaseContentStoreProviderTest{
    private AddContentItemAction addContentItemAction; 
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.addContentItemAction = new AddContentItemAction();
        this.addContentItemAction.setContentStoreProvider(contentStoreProvider);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetContentStoreProvider() throws Exception{
        Assert.assertNotNull(this.addContentItemAction.getContentStoreProvider().getContentStore());
    }

    @Test
    public void testExecute() throws Exception{
        ContentItem contentItem = new ContentItem();
        contentItem.setContentId("test-content");
        MultipartFile file = new MockMultipartFile("test-content.jpg");
        
        Space space = new Space();
        space.setSpaceId("test-space");
        contentItem.setSpaceId(space.getSpaceId());
        contentItem.setFile(file);
        
        MessageContext messageContext = new MockMessageContext();
        this.addContentItemAction.execute(contentItem, space, messageContext);
    }

    public class MockMultipartFile implements MultipartFile {
        private String filename;

        public MockMultipartFile(String filename){
            this.filename = filename;
        }
        public byte[] getBytes() throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

        public String getContentType() {
            // TODO Auto-generated method stub
            return "image/jpeg";
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(new byte[0]);
        }

        public String getName() {
            return filename;
        }

        public String getOriginalFilename() {
            return filename;
        }

        public long getSize() {
            return filename.length()*1024;
        }

        public boolean isEmpty() {
            return false;
        }

        public void transferTo(File dest) throws IOException,
                IllegalStateException {
        }
    }
    
    public class MockMessageContext implements MessageContext{
        
        public boolean hasErrorMessages() {
            // TODO Auto-generated method stub
            return false;
        }
        
        public Message[] getMessagesBySource(Object source) {
            // TODO Auto-generated method stub
            return null;
        }
        
        public Message[] getMessagesByCriteria(MessageCriteria criteria) {
            // TODO Auto-generated method stub
            return null;
        }
        
        public Message[] getAllMessages() {
            // TODO Auto-generated method stub
            return null;
        }
        
        public void clearMessages() {
            // TODO Auto-generated method stub
            
        }
        
        public void addMessage(MessageResolver messageResolver) {
            // TODO Auto-generated method stub
            
        }
    }

}

