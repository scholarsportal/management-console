/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

/**
 * @author Andrew Woods
 *         Date: Dec 7, 2010
 */
public interface IdUtil {

    /**
     * This method initializes the IdUtil to the underlying tables.
     *
     * @param host of id-generator
     * @param port of id-generator
     * @param context of id-generator
     * @param username of id-generator
     * @param password of id-generator
     */
    public void initialize(String host,
                           String port,
                           String context,
                           String username,
                           String password,
                           DuracloudAccountRepo accountRepo,
                           DuracloudUserInvitationRepo userInvitationRepo,
                           DuracloudInstanceRepo instanceRepo,
                           DuracloudServerImageRepo serverImageRepo,
                           DuracloudComputeProviderAccountRepo computeProviderAccountRepo,
                           DuracloudStorageProviderAccountRepo storageProviderAccountRepo,
                           DuracloudServerDetailsRepo serverDetailsRepo,
                           DuracloudAccountClusterRepo accountClusterRepo);

    public int newAccountId();

    public int newUserId();

    public int newGroupId();

    public int newRightsId();

    public int newUserInvitationId();

    public int newInstanceId();

    public int newServerImageId();

    public int newComputeProviderAccountId();

    public int newStorageProviderAccountId();

    public int newServerDetailsId();

    public int newAccountClusterId();

}
