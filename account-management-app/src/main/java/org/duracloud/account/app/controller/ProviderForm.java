/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import java.util.List;

import org.duracloud.storage.domain.StorageProviderType;

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
