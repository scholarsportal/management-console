/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.mgmt;

import java.util.List;

import org.duracloud.common.model.Credential;
import org.duracloud.mainwebapp.domain.model.StorageAcct;
import org.duracloud.mainwebapp.domain.model.StorageProvider;
import org.duracloud.storage.domain.StorageProviderType;

/**
 * <pre>
 * This interface encapsulates the implementation of:
 *  -navigating the customer-account repositories and
 *  -applying business logic / exception handling.
 *
 * </pre>
 *
 * @author Andrew Woods
 */
public interface StorageAcctManager {

    /**
     * This method returns a list of storage-provider-accounts associated with
     * the provided DuraCloud Account ID.
     *
     * @param DuraCloud
     *        Account Id
     * @return
     */
    public List<StorageAcct> getStorageProviderAccountsByDuraAcctId(int duraAcctId);

    public int saveStorageAcct(StorageAcct storageAcct) throws Exception;

    public int saveCredentialForStorageAcct(Credential storageAcctCred,
                                            int storageAcctId) throws Exception;

    public StorageProvider findStorageProviderForStorageAcct(int storageAcctId)
            throws Exception;

    public StorageAcct findStorageAccountAndLoadCredential(int storageAcctId)
            throws Exception;

    public int findStorageProviderIdByProviderType(StorageProviderType providerType)
            throws Exception;

    public boolean isStorageNamespaceTaken(String storageAcctNamespace);

}