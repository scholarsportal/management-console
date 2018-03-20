/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;

import org.duracloud.account.util.StorageProviderTypeUtil;
import org.duracloud.storage.domain.StorageProviderType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Daniel Bernstein
 * Date: Mar 5, 2012
 */
@SuppressWarnings("serial")
@Component("fullAccountForm")
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class FullAccountForm implements Serializable {

    private List<StorageProviderType> storageProviders;

    @NotNull(message = "A primary storage provider must be specified")
    private StorageProviderType primaryStorageProvider = null;

    public StorageProviderType getPrimaryStorageProvider() {
        return primaryStorageProvider;
    }

    public void setPrimaryStorageProvider(StorageProviderType primaryStorageProvider) {
        this.primaryStorageProvider = primaryStorageProvider;
    }

    public List<StorageProviderType> getStorageProviderOptions() {
        return StorageProviderTypeUtil.getAvailableTypes();
    }

    public List<StorageProviderType> getStorageProviders() {
        return storageProviders;
    }

    public void setStorageProviders(List<StorageProviderType> storageProviders) {
        this.storageProviders = storageProviders;
    }

}
