package org.duracloud.duradmin.contentstore;

import java.util.List;

import org.duracloud.client.ContentStore;
import org.junit.Assert;
import org.junit.Test;


public class ContentStoreSelectorTest extends ContentStoreProviderTestBase {
    
    @Test
    public void testSelectStore() throws Exception {
        ContentStoreSelector selector = this.contentStoreProvider.getContentStoreSelector();
        Assert.assertNotNull(selector);
        String storeId = selector.getSelectedId();
        Assert.assertNotNull(storeId);
        List<ContentStore> stores = selector.getContentStores();
        for(ContentStore store : stores){
            String sId= store.getStoreId();
            if(sId != storeId){
                Assert.assertNotSame(sId,contentStoreProvider.getContentStore().getStoreId());
                selector.setSelectedId(sId);
                Assert.assertSame(sId,contentStoreProvider.getContentStore().getStoreId());
                break;
            }
        }
    }
}
