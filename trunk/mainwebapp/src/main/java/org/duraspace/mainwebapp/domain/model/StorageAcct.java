
package org.duraspace.mainwebapp.domain.model;

import org.duraspace.common.model.Credential;
import org.duraspace.storage.domain.StorageProviderType;

public class StorageAcct {

    private int id;

    private int isPrimary;

    private String namespace;

    private StorageProviderType storageProviderType;

    private int storageProviderId;

    private int storageCredentialId;

    private int duraAcctId;

    // The attributes below are loaded from IDs above.
    private StorageProvider storageProvider;

    private Credential storageProviderCredential;

    public boolean hasId() {
        return id > 0;
    }

    public boolean hasStorageProviderId() {
        return storageProviderId > 0;
    }

    public boolean hasStorageCredentialId() {
        return storageCredentialId > 0;
    }

    public boolean hasDuraAcctId() {
        return duraAcctId > 0;
    }

    public int getIsPrimary() {
        return isPrimary;
    }

    public void setPrimary(int isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getIsPrimaryAsString() {
        return isPrimary == 1 ? "true" : "false";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public int getStorageProviderId() {
        return storageProviderId;
    }

    public void setStorageProviderId(int storageProviderId) {
        this.storageProviderId = storageProviderId;
    }

    public int getDuraAcctId() {
        return duraAcctId;
    }

    public void setDuraAcctId(int duraAcctId) {
        this.duraAcctId = duraAcctId;
    }

    public int getStorageCredentialId() {
        return storageCredentialId;
    }

    public void setStorageCredentialId(int storageCredentialId) {
        this.storageCredentialId = storageCredentialId;
    }

    public StorageProviderType getStorageProviderType() {
        return storageProviderType;
    }

    public void setStorageProviderType(StorageProviderType storageProviderType) {
        this.storageProviderType = storageProviderType;
    }

    public void setStorageProviderType(String storageProviderType) {
        this.storageProviderType =
                StorageProviderType.fromString(storageProviderType);
    }

    public StorageProvider getStorageProvider() {
        return storageProvider;
    }

    public void setStorageProvider(StorageProvider storageProvider) {
        this.storageProvider = storageProvider;
    }

    public Credential getStorageProviderCredential() {
        return storageProviderCredential;
    }

    public void setStorageProviderCredential(Credential storageProviderCredential) {
        this.storageProviderCredential = storageProviderCredential;
    }

}
