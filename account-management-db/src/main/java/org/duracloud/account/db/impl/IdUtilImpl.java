/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.impl;

import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRightsRepo;
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

    public void initialize(DuracloudUserRepo userRepo,
                           DuracloudAccountRepo accountRepo,
                           DuracloudRightsRepo rightsRepo,
                           DuracloudUserInvitationRepo userInvitationRepo) {
        this.accountId = max(accountRepo.getIds());
        this.userId = max(userRepo.getIds());
        this.rightsId = max(rightsRepo.getIds());
        this.userInvitationId = max(userInvitationRepo.getIds());
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

}
