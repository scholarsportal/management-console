
package org.duracloud.duradmin.domain;

import java.io.Serializable;

public class StorageProvider
        implements Serializable {

    private static final long serialVersionUID = 1L;

    public String getStorageProviderId() {
        return storageProviderId;
    }

    public void setStorageProviderId(String storageProviderId) {
        this.storageProviderId = storageProviderId;
    }

    private String storageProviderId;
}
