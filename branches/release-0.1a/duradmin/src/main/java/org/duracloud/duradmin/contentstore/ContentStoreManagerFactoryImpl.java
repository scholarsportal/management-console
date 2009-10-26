
package org.duracloud.duradmin.contentstore;

import org.duracloud.client.ContentStoreManager;
import org.duracloud.client.ContentStoreManagerImpl;
import org.duracloud.duradmin.config.DuradminConfig;

public class ContentStoreManagerFactoryImpl
        implements ContentStoreManagerFactory {

    public ContentStoreManager create() throws Exception {
            return new ContentStoreManagerImpl(DuradminConfig.getHost(),
                                               DuradminConfig.getPort());
    }
}
