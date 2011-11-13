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
                           DuracloudGroupRepo groupRepo,
                           DuracloudAccountRepo accountRepo,
                           DuracloudRightsRepo rightsRepo,
                           DuracloudUserInvitationRepo userInvitationRepo,
                           DuracloudInstanceRepo instanceRepo,
                           DuracloudServerImageRepo serverImageRepo,
                           DuracloudComputeProviderAccountRepo computeProviderAccountRepo,
                           DuracloudStorageProviderAccountRepo storageProviderAccountRepo,
                           DuracloudServiceRepositoryRepo serviceRepositoryRepo);

    public int newAccountId();

    public int newUserId();

    public int newGroupId();

    public int newRightsId();

    public int newUserInvitationId();

    public int newInstanceId();

    public int newServerImageId();

    public int newComputeProviderAccountId();

    public int newStorageProviderAccountId();

    public int newServiceRepositoryId();
}
