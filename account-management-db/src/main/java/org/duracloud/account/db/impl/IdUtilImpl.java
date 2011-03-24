/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.impl;

import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudServiceRepositoryRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBUninitializedException;

import java.util.Collection;
import java.util.Collections;

/**
 * @author: Bill Branan
 *          Date: Dec 3, 2010
 */
public class IdUtilImpl implements IdUtil {

    private int accountId = -1;
    private int userId = -1;
    private int rightsId = -1;
    private int userInvitationId = -1;
    private int instanceId = -1;
    private int serverImageId = -1;
    private int computeProviderAccountId = -1;
    private int storageProviderAccountId = -1;
    private int serviceRepositoryId = -1;

    public void initialize(DuracloudUserRepo userRepo,
                           DuracloudAccountRepo accountRepo,
                           DuracloudRightsRepo rightsRepo,
                           DuracloudUserInvitationRepo userInvitationRepo,
                           DuracloudInstanceRepo instanceRepo,
                           DuracloudServerImageRepo serverImageRepo,
                           DuracloudComputeProviderAccountRepo computeProviderAccountRepo,
                           DuracloudStorageProviderAccountRepo storageProviderAccountRepo,
                           DuracloudServiceRepositoryRepo serviceRepositoryRepo) {
        this.accountId = max(accountRepo.getIds());
        this.userId = max(userRepo.getIds());
        this.rightsId = max(rightsRepo.getIds());
        this.userInvitationId = max(userInvitationRepo.getIds());
        this.instanceId = max(instanceRepo.getIds());
        this.serverImageId = max(serverImageRepo.getIds());
        this.computeProviderAccountId = max(computeProviderAccountRepo.getIds());
        this.storageProviderAccountId = max(storageProviderAccountRepo.getIds());
        this.serviceRepositoryId = max(serviceRepositoryRepo.getIds());
    }

    private int max(Collection<? extends Integer> c) {
        // this check is necessary because Collections.max(int)
        // throws a NoSuchElementException when the collection
        // is empty.
        return c.isEmpty() ? 0 : Collections.max(c);
    }

    private void checkInitialized() {
        if (accountId < 0 || userId < 0 || rightsId < 0 || userInvitationId < 0) {
            throw new DBUninitializedException("IdUtil must be initialized");
        }
    }

    @Override
    public synchronized int newAccountId() {
        checkInitialized();
        return ++accountId;
    }

    @Override
    public synchronized int newUserId() {
        checkInitialized();
        return ++userId;
    }

    @Override
    public synchronized int newRightsId() {
        checkInitialized();
        return ++rightsId;
    }

    @Override
    public synchronized int newUserInvitationId() {
        checkInitialized();
        return ++userInvitationId;
    }

    @Override
    public synchronized int newInstanceId() {
        checkInitialized();
        return ++instanceId;
    }

    @Override
    public int newServerImageId() {
        checkInitialized();
        return ++serverImageId;
    }

    @Override
    public int newComputeProviderAccountId() {
        checkInitialized();
        return ++computeProviderAccountId;
    }

    @Override
    public int newStorageProviderAccountId() {
        checkInitialized();
        return ++storageProviderAccountId;
    }

    @Override
    public int newServiceRepositoryId() {
        checkInitialized();
        return ++serviceRepositoryId;
    }
}
