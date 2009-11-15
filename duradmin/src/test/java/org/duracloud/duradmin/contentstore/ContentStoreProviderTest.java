
package org.duracloud.duradmin.contentstore;

import org.junit.Test;
import org.springframework.util.Assert;

public class ContentStoreProviderTest
        extends ContentStoreProviderTestBase {

    @Test
    public void testGetContentStoreSelector() {
        Assert.notNull(this.contentStoreProvider.getContentStoreSelector());
    }

    @Test
    public void testGetContentStore() throws Exception {
        Assert.notNull(this.contentStoreProvider.getContentStore());
    }

}
