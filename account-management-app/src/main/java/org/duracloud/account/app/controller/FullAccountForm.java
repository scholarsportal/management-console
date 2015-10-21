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

import org.duracloud.account.util.StorageProviderTypeUtil;
import org.duracloud.storage.domain.StorageProviderType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;


/**
 * 
 * @author Daniel Bernstein 
 *         Date: Mar 5, 2012
 */
@SuppressWarnings("serial")
@Component("fullAccountForm")
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class FullAccountForm  implements Serializable{
    
    private List<StorageProviderType> secondaryStorageProviders;

    public List<StorageProviderType> getSecondaryStorageProviderOptions() {
        return StorageProviderTypeUtil.getAvailableSecondaryTypes();
    }

    public List<StorageProviderType> getSecondaryStorageProviders() {
        return secondaryStorageProviders;
    }

    public void
        setSecondaryStorageProviders(List<StorageProviderType> secondaryStorageProviders) {
        this.secondaryStorageProviders = secondaryStorageProviders;
    }

}
