
package org.duraspace.mainwebapp.domain.model;

import org.duraspace.serviceprovider.domain.ComputeProviderType;

public class ComputeProvider {

    private int id;

    private String providerName;

    private ComputeProviderType providerType;

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

    public ComputeProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = ComputeProviderType.fromString(providerType);
    }

    public void setProviderType(ComputeProviderType providerType) {
        this.providerType = providerType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
