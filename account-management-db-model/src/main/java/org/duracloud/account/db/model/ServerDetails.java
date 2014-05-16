/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Erik Paulsson
 *         Date: 7/10/13
 */
@Entity
public class ServerDetails extends BaseEntity {

    /**
     * The ComputeProviderAccount which is the compute provider for
     * an instance
     */
    @OneToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="compute_provider_account_id", nullable=false)
    private ComputeProviderAccount computeProviderAccount;

    /**
     * The StorageProviderAccount which is used for primary storage
     */
    @OneToOne(fetch= FetchType.EAGER, optional=false)
    @JoinColumn(name="primary_storage_provider_account_id", nullable=false)
    private StorageProviderAccount primaryStorageProviderAccount;

    /**
     * The StorageProviderAccounts which are used for secondary storage
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "server_details_id")
    private Set<StorageProviderAccount> secondaryStorageProviderAccounts =
            new HashSet<StorageProviderAccount>();

    /*
     * The service plan for this account
     */
    @Enumerated(EnumType.STRING)
    private ServicePlan servicePlan;

    public ComputeProviderAccount getComputeProviderAccount() {
        return computeProviderAccount;
    }

    public void setComputeProviderAccount(ComputeProviderAccount computeProviderAccount) {
        this.computeProviderAccount = computeProviderAccount;
    }

    public StorageProviderAccount getPrimaryStorageProviderAccount() {
        return primaryStorageProviderAccount;
    }

    public void setPrimaryStorageProviderAccount(StorageProviderAccount primaryStorageProviderAccount) {
        this.primaryStorageProviderAccount = primaryStorageProviderAccount;
    }

    public Set<StorageProviderAccount> getSecondaryStorageProviderAccounts() {
        return secondaryStorageProviderAccounts;
    }

    public void setSecondaryStorageProviderAccounts(Set<StorageProviderAccount> secondaryStorageProviderAccounts) {
        this.secondaryStorageProviderAccounts = secondaryStorageProviderAccounts;
    }

    public ServicePlan getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(ServicePlan servicePlan) {
        this.servicePlan = servicePlan;
    }
}
