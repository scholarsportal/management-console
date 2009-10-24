package org.duracloud.duradmin.webflow.content;

import java.io.ByteArrayInputStream;

import org.duracloud.duradmin.contentstore.BaseContentStoreProviderTest;
import org.duracloud.duradmin.domain.ContentItem;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.mock.MockMessageContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.binding.message.MessageContext;
import org.springframework.mock.web.MockMultipartFile;
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
        MultipartFile file = new MockMultipartFile("test-content.jpg", new ByteArrayInputStream("test".getBytes()));
        Space space = new Space();
        space.setSpaceId("test-space");
        contentItem.setSpaceId(space.getSpaceId());
        contentItem.setFile(file);
        MessageContext messageContext = new MockMessageContext();
        this.addContentItemAction.execute(contentItem, space, messageContext);
    }




}

