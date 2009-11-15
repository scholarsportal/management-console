
package org.duracloud.duradmin.contentstore;

import java.util.ArrayList;
import java.util.List;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ContentStoreManager;

public class ContentStoreSelector {

    private String selectedId;

    private ContentStoreManager contentStoreManager;

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }

    public String getSelectedId() throws ContentStoreException {
        if (this.selectedId == null) {
            ContentStore store =
                    this.contentStoreManager.getPrimaryContentStore();
            setSelectedId(store.getStoreId());
        }
        return this.selectedId;
    }

    public ContentStoreManager getContentStoreManager() {
        return contentStoreManager;
    }

    public void setContentStoreManager(ContentStoreManager contentStoreManager) {
        this.contentStoreManager = contentStoreManager;
    }

    public List<ContentStore> getContentStores() throws ContentStoreException {
        List<ContentStore> stores = new ArrayList<ContentStore>();
        stores.addAll(this.contentStoreManager.getContentStores().values());
        return stores;
    }

}
