/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    private boolean useReducedRedundancy = false;
    
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

    public boolean isUseReducedRedundancy() {
        return useReducedRedundancy;
    }

    public void setUseReducedRedundancy(boolean useReducedRedundancy) {
        this.useReducedRedundancy = useReducedRedundancy;
    }

}
