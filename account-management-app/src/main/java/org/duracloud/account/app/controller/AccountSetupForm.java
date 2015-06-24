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

import org.duracloud.account.db.model.ComputeProviderAccount;
import org.duracloud.account.db.model.StorageProviderAccount;
import org.duracloud.storage.domain.StorageProviderType;
import org.hibernate.validator.constraints.NotBlank;

public class AccountSetupForm {

    @Valid
    private StorageProviderSettings primaryStorageProviderSettings;

    @Valid
    private List<StorageProviderSettings> secondaryStorageProviderSettingsList;

    private boolean computeCredentialsSame;

    @NotBlank(message = "Compute account's username is required")
    private String computeUsername;

    @NotBlank(message = "Compute account's password is required")
    private String computePassword;

    @NotBlank(message = "Elastic IP is required")
    private String computeElasticIP;

    @NotBlank(message = "Keypair is required")
    private String computeKeypair;

    @NotBlank(message = "Security group is required")
    private String computeSecurityGroup;

    public AccountSetupForm(StorageProviderAccount primary,
            List<StorageProviderAccount> secondaryList,
            ComputeProviderAccount compute) {
        this();

        this.primaryStorageProviderSettings = createStorageProviderSettings(primary);

        for (StorageProviderAccount spa : secondaryList) {
            this.secondaryStorageProviderSettingsList
                    .add(createStorageProviderSettings(spa));
        }

        if (compute != null) {
            this.computeUsername = compute.getUsername();
            this.computePassword = compute.getPassword();
            this.computeElasticIP = compute.getElasticIp();
            this.computeKeypair = compute.getKeypair();
            this.computeSecurityGroup = compute.getSecurityGroup();
        }
    }

    private StorageProviderSettings createStorageProviderSettings(
            StorageProviderAccount spAccount) {
            return new StorageProviderSettings(spAccount);
    }

    public AccountSetupForm() {
        this.primaryStorageProviderSettings = new StorageProviderSettings();

        this.secondaryStorageProviderSettingsList = new LinkedList<StorageProviderSettings>();

    }

    public boolean isComputeCredentialsSame() {
        return computeCredentialsSame;
    }

    public void setComputeCredentialsSame(boolean computeCredentialsSame) {
        this.computeCredentialsSame = computeCredentialsSame;
    }

    public String getComputeUsername() {
        return computeUsername;
    }

    public void setComputeUsername(String computeUsername) {
        this.computeUsername = computeUsername;
    }

    public String getComputePassword() {
        return computePassword;
    }

    public void setComputePassword(String computePassword) {
        this.computePassword = computePassword;
    }

    public String getComputeElasticIP() {
        return computeElasticIP;
    }

    public void setComputeElasticIP(String computeElasticIP) {
        this.computeElasticIP = computeElasticIP;
    }

    public String getComputeKeypair() {
        return computeKeypair;
    }

    public void setComputeKeypair(String computeKeypair) {
        this.computeKeypair = computeKeypair;
    }

    public String getComputeSecurityGroup() {
        return computeSecurityGroup;
    }

    public void setComputeSecurityGroup(String computeSecurityGroup) {
        this.computeSecurityGroup = computeSecurityGroup;
    }

    public StorageProviderSettings getPrimaryStorageProviderSettings() {
        return primaryStorageProviderSettings;
    }

    public void setPrimaryStorageProviderSettings(
            StorageProviderSettings primaryStorageProviderSettings) {
        this.primaryStorageProviderSettings = primaryStorageProviderSettings;
    }

    public List<StorageProviderSettings> getSecondaryStorageProviderSettingsList() {
        return secondaryStorageProviderSettingsList;
    }

    public void setSecondaryStorageCredentailsList(
            List<StorageProviderSettings> secondaryStorageProviderSettingsList) {
        this.secondaryStorageProviderSettingsList = secondaryStorageProviderSettingsList;
    }

    public static class StorageProviderSettings {
        private Long id;
        @NotBlank(message = "Username is required")
        private String username;
        @NotBlank(message = "Password is required")
        private String password;
        
        @NotBlank(message = "Storage Limit is required")
        @Digits(integer=3, fraction=0)
        private String storageLimit = "1";
        
        private StorageProviderType providerType;

        private Map<String, String> properties = new HashMap<>();


        public StorageProviderSettings(
                StorageProviderAccount storageProviderAccount) {
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
            this.storageLimit = storageProviderAccount.getStorageLimit()+"";
            
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
