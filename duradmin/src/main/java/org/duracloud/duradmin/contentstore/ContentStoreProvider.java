package org.duracloud.duradmin.contentstore;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ContentStoreManager;


public class ContentStoreProvider {
    
    private ContentStoreManager contentStoreManager;

    private ContentStoreSelector contentStoreSelector;
    
    
    public ContentStoreSelector getContentStoreSelector() {
        return contentStoreSelector;
    }

    
    public void setContentStoreSelector(ContentStoreSelector contentStoreSelector) {
        this.contentStoreSelector = contentStoreSelector;
    }

    public ContentStoreManager getContentStoreManager() {
        return contentStoreManager;
    }

    public void setContentStoreManager(ContentStoreManager contentStoreManager) {
        this.contentStoreManager = contentStoreManager;
    }
    
    public ContentStore getContentStore() throws ContentStoreException {
        String contentStoreId = this.contentStoreSelector.getSelectedId();
        return this.contentStoreManager.getContentStore(contentStoreId);
    }

}
