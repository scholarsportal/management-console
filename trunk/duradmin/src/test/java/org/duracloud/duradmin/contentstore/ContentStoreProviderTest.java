package org.duracloud.duradmin.contentstore;

import org.duracloud.duradmin.mock.contentstore.MockContentStoreManagerFactoryImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;


public class ContentStoreProviderTest {
    
    protected ContentStoreProvider contentStoreProvider;
    
    
    @Before
    public void setUp() throws Exception {
        this.contentStoreProvider = new ContentStoreProvider();
        this.contentStoreProvider.setContentStoreManager(new MockContentStoreManagerFactoryImpl().create());
        ContentStoreSelector selector = new ContentStoreSelector();
        selector.setContentStoreManager(this.contentStoreProvider.getContentStoreManager());
        this.contentStoreProvider.setContentStoreSelector(selector);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetContentStoreSelector() {
        Assert.notNull(this.contentStoreProvider.getContentStoreSelector());
    }

    @Test
    public void testGetContentStore() throws Exception{
        Assert.notNull(this.contentStoreProvider.getContentStore());
    }

    

}
