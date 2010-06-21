
package org.duracloud.duradmin.webflow.space;

import org.duracloud.client.ContentStore.AccessType;
import org.duracloud.duradmin.contentstore.ContentStoreProviderTestBase;
import org.duracloud.duradmin.domain.Space;
import org.duracloud.mock.MockMessageContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.binding.message.MessageContext;

public class AddSpaceActionTest
        extends ContentStoreProviderTestBase {

    private AddSpaceAction addSpaceAction;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.addSpaceAction = new AddSpaceAction();
        this.addSpaceAction.setContentStoreProvider(contentStoreProvider);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetContentStoreProvider() throws Exception {
        Assert.assertNotNull(this.addSpaceAction.getContentStoreProvider()
                .getContentStore());
    }

    @Test
    public void testExecute() throws Exception {
        Space space = new Space();
        space.setSpaceId("test-space");
        space.setAccess(AccessType.OPEN.name());

        MessageContext messageContext = new MockMessageContext();
        boolean result = this.addSpaceAction.execute(space, messageContext);
        Assert.assertTrue(result);
    }
}
