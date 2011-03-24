/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

/**
 * @author Andrew Woods
 *         Date: Dec 7, 2010
 */
public interface IdUtil {

    public void initialize(DuracloudUserRepo userRepo,
                           DuracloudAccountRepo accountRepo,
                           DuracloudRightsRepo rightsRepo,
                           DuracloudUserInvitationRepo userInvitationRepo,
                           DuracloudInstanceRepo instanceRepo,
                           DuracloudServerImageRepo serverImageRepo,
                           DuracloudComputeProviderAccountRepo computeProviderAccountRepo,
                           DuracloudStorageProviderAccountRepo storageProviderAccountRepo,
                           DuracloudServiceRepositoryRepo serviceRepositoryRepo);

    int newAccountId();

    int newUserId();

    int newRightsId();

    int newUserInvitationId();

    int newInstanceId();

    int newServerImageId();

    int newComputeProviderAccountId();

    int newStorageProviderAccountId();

    int newServiceRepositoryId();
}
