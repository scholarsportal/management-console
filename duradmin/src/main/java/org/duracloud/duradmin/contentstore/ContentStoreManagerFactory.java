
package org.duracloud.duradmin.contentstore;

import org.duracloud.client.ContentStoreManager;

public interface ContentStoreManagerFactory {

    public ContentStoreManager create() throws Exception;
}
