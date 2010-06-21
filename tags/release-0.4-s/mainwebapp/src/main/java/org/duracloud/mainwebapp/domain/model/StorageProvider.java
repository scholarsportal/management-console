/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.model;

import org.duracloud.storage.domain.StorageProviderType;

public class StorageProvider {

    private int id;

    private String providerName;

    private StorageProviderType providerType;

    private String url;

    public boolean hasId() {
        return id > 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public StorageProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = StorageProviderType.fromString(providerType);
    }

    public void setProviderType(StorageProviderType providerType) {
        this.providerType = providerType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
