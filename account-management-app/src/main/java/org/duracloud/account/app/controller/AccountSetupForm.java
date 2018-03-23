/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.Digits;

import org.duracloud.account.db.model.StorageProviderAccount;
import org.duracloud.storage.domain.StorageProviderType;
import org.hibernate.validator.constraints.NotBlank;

public class AccountSetupForm {

    @Valid
    private StorageProviderSettings primaryStorageProviderSettings;

    @Valid
    private List<StorageProviderSettings> secondaryStorageProviderSettingsList;

    public AccountSetupForm(StorageProviderAccount primary,
                            List<StorageProviderAccount> secondaryList) {
        this();

        this.primaryStorageProviderSettings = createStorageProviderSettings(primary);

        for (StorageProviderAccount spa : secondaryList) {
            this.secondaryStorageProviderSettingsList.add(createStorageProviderSettings(spa));
        }

    }

    private StorageProviderSettings createStorageProviderSettings(StorageProviderAccount spAccount) {
        return new StorageProviderSettings(spAccount);
    }

    public AccountSetupForm() {
        this.primaryStorageProviderSettings = new StorageProviderSettings();

        this.secondaryStorageProviderSettingsList = new LinkedList<StorageProviderSettings>();

    }

    public StorageProviderSettings getPrimaryStorageProviderSettings() {
        return primaryStorageProviderSettings;
    }

    public void setPrimaryStorageProviderSettings(StorageProviderSettings primaryStorageProviderSettings) {
        this.primaryStorageProviderSettings = primaryStorageProviderSettings;
    }

    public List<StorageProviderSettings> getSecondaryStorageProviderSettingsList() {
        return secondaryStorageProviderSettingsList;
    }

    public void setSecondaryStorageCredentailsList(List<StorageProviderSettings> secondaryStorageProviderSettingsList) {
        this.secondaryStorageProviderSettingsList = secondaryStorageProviderSettingsList;
    }

    public static class StorageProviderSettings {
        private Long id;

        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        @NotBlank(message = "Storage Limit is required")
        @Digits(integer = 3, fraction = 0)
        private String storageLimit = "1";

        private StorageProviderType providerType;

        private Map<String, String> properties = new HashMap<>();

        public StorageProviderSettings(StorageProviderAccount storageProviderAccount) {
            this.username = storageProviderAccount.getUsername();
            this.password = storageProviderAccount.getPassword();
            this.id = storageProviderAccount.getId();
            this.providerType = storageProviderAccount.getProviderType();

            if ("TBD".equals(this.username)) {
                this.username = null;
            }

            if ("TBD".equals(this.password)) {
                this.password = null;
            }

            this.properties = storageProviderAccount.getProperties();
            this.storageLimit = storageProviderAccount.getStorageLimit() + "";

        }

        public StorageProviderSettings() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public StorageProviderType getProviderType() {
            return this.providerType;
        }

        public void setProviderType(StorageProviderType providerType) {
            this.providerType = providerType;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }

        public void setProperties(Map<String, String> properties) {
            this.properties = properties;
        }

        public String getStorageLimit() {
            return storageLimit;
        }

        public void setStorageLimit(String storageLimit) {
            this.storageLimit = storageLimit;
        }

    }
}
