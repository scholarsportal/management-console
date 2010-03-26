
package org.duracloud.duradmin.contentstore;

import org.duracloud.client.ContentStoreManager;
import org.duracloud.duradmin.mock.contentstore.MockContentStoreManagerFactoryImpl;
import org.junit.After;
import org.junit.Before;

public class ContentStoreProviderTestBase {

    protected ContentStoreProvider contentStoreProvider;

    @Before
    public void setUp() throws Exception {
        ContentStoreManager contentStoreManager = new MockContentStoreManagerFactoryImpl()
            .create();
        ContentStoreSelector contentStoreSelector = new ContentStoreSelector();
        this.contentStoreProvider = new ContentStoreProvider(contentStoreManager,
                                                             contentStoreSelector);
    }

    @After
    public void tearDown() throws Exception {
    }
}
