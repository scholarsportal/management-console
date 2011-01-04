package org.duracloud.account.app.controller;

import org.duracloud.storage.domain.StorageProviderType;

import java.util.List;

public class ProviderForm {
    private String provider;

    private List<StorageProviderType> storageProviders;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public List<StorageProviderType> getStorageProviders() {
        return storageProviders;
    }

    public void setStorageProviders(List<StorageProviderType> storageProviders) {
        this.storageProviders = storageProviders;
    }
}
