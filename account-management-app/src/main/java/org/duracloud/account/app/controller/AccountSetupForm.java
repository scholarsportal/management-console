package org.duracloud.account.app.controller;

import java.util.LinkedList;
import java.util.List;

import javax.validation.Valid;

import org.duracloud.account.db.model.ComputeProviderAccount;
import org.duracloud.account.db.model.StorageProviderAccount;
import org.duracloud.storage.domain.StorageProviderType;
import org.hibernate.validator.constraints.NotBlank;

public class AccountSetupForm {
    
    @Valid
    private StorageCredentials primaryStorageCredentials;
    
    @Valid
    private List<StorageCredentials> secondaryStorageCredentailsList;
    
    private boolean computeCredentialsSame;

    @NotBlank(message="Compute account's username is required")
    private String computeUsername;

    @NotBlank(message="Compute account's password is required")
    private String computePassword;

    @NotBlank(message="Elastic IP is required")
    private String computeElasticIP;

    @NotBlank(message="Keypair is required")
    private String computeKeypair;

    @NotBlank(message="Security group is required")
    private String computeSecurityGroup;

    @NotBlank(message="Audit queue is required")
    private String auditQueue;
    
    
    public AccountSetupForm(
        StorageProviderAccount primary,
        List<StorageProviderAccount> secondaryList,
        ComputeProviderAccount compute) {
        this();

        this.primaryStorageCredentials = new StorageCredentials(primary);

        for (StorageProviderAccount spa : secondaryList) {
            this.secondaryStorageCredentailsList.add(new StorageCredentials(spa));
        }
        
        if(compute != null){
            this.computeUsername = compute.getUsername();
            this.computePassword = compute.getPassword();
            this.computeElasticIP = compute.getElasticIp();
            this.computeKeypair = compute.getKeypair();
            this.computeSecurityGroup = compute.getSecurityGroup();
            this.auditQueue = compute.getAuditQueue();
        }
    }

    public AccountSetupForm(){
        this.primaryStorageCredentials = new StorageCredentials();

        this. secondaryStorageCredentailsList =
            new LinkedList<StorageCredentials>();

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
    
    public String getAuditQueue() {
        return auditQueue;
    }
    
    public void setAuditQueue(String auditQueue) {
        this.auditQueue = auditQueue;
    }
    
    public StorageCredentials getPrimaryStorageCredentials() {
        return primaryStorageCredentials;
    }


    public void setPrimaryStorageCredentials(StorageCredentials primaryStorageCredentials) {
        this.primaryStorageCredentials = primaryStorageCredentials;
    }

    public List<StorageCredentials> getSecondaryStorageCredentailsList() {
        return secondaryStorageCredentailsList;
    }


    public void
        setSecondaryStorageCredentailsList(List<StorageCredentials> secondaryStorageCredentailsList) {
        this.secondaryStorageCredentailsList = secondaryStorageCredentailsList;
    }

    public static class StorageCredentials {
        private Long id;
        @NotBlank(message="Username is required")
        private String username;
        @NotBlank(message="Password is required")
        private String password;
        private StorageProviderType providerType;

        public StorageCredentials(StorageProviderAccount storageProviderAccount) {
            this.username = storageProviderAccount.getUsername();
            this.password = storageProviderAccount.getPassword();
            this.id = storageProviderAccount.getId();
            this.providerType = storageProviderAccount.getProviderType();
            
            if("TBD".equals(this.username)){
                this.username = null;
            }

            if("TBD".equals(this.password)){
                this.password = null;
            }
        }
        
        public StorageCredentials() {}

        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }
        
        public StorageProviderType getProviderType(){
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
    }
}
