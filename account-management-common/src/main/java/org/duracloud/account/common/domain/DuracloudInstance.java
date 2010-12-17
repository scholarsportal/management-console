/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import java.util.Set;

/**
 * @author Bill Branan
 *         Date: Dec 17, 2010
 */
public class DuracloudInstance extends BaseDomainData {

    /**
     * The ID of the Image on which this instance is based.
     */
    private int imageId;

    /**
     * The host name at which this instance is available.
     */
    private String hostName;

    /**
     * The identifier value assigned to this machine instance by the compute
     * provider. This ID is used when starting, stopping, or restarting the
     * server on which the DuraCloud software is running.
     */
    private String providerInstanceId;

    /**
     * The ID of a ProviderAccount which is used for primary storage
     */
    private int primaryStorageProviderAccountId;

    /**
     * The IDs of all ProviderAccounts which are used for secondary storage
     */
    private Set<Integer> secondaryStorageProviderAccountIds;

    /**
     * The IDs of all ServiceRepositories used to store service binaries
     */
    private Set<Integer> serviceRepositoryIds;

    /**
     * The username of the DuraCloud root user which can be used to perform
     * actions on this DuraCloud instance
     */
    private String dcRootUsername;

    /**
     * The password of the DuraCloud root user which can be used to perform
     * actions on this DuraCloud instance
     */
    private String dcRootPassword;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getProviderInstanceId() {
        return providerInstanceId;
    }

    public void setProviderInstanceId(String providerInstanceId) {
        this.providerInstanceId = providerInstanceId;
    }

    public int getPrimaryStorageProviderAccountId() {
        return primaryStorageProviderAccountId;
    }

    public void setPrimaryStorageProviderAccountId(
        int primaryStorageProviderAccountId) {
        this.primaryStorageProviderAccountId = primaryStorageProviderAccountId;
    }

    public Set<Integer> getSecondaryStorageProviderAccountIds() {
        return secondaryStorageProviderAccountIds;
    }

    public void setSecondaryStorageProviderAccountIds(
        Set<Integer> secondaryStorageProviderAccountIds) {
        this.secondaryStorageProviderAccountIds = secondaryStorageProviderAccountIds;
    }

    public Set<Integer> getServiceRepositoryIds() {
        return serviceRepositoryIds;
    }

    public void setServiceRepositoryIds(Set<Integer> serviceRepositoryIds) {
        this.serviceRepositoryIds = serviceRepositoryIds;
    }

    public String getDcRootUsername() {
        return dcRootUsername;
    }

    public void setDcRootUsername(String dcRootUsername) {
        this.dcRootUsername = dcRootUsername;
    }

    public String getDcRootPassword() {
        return dcRootPassword;
    }

    public void setDcRootPassword(String dcRootPassword) {
        this.dcRootPassword = dcRootPassword;
    }
}
