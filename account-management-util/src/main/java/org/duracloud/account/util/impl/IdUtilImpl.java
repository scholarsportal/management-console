/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.util.IdUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;

import java.util.Collections;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: Dec 3, 2010
 */
public class IdUtilImpl implements IdUtil {

    private int accountId = -1;
    private int userId = -1;
    private int rightsId = -1;

    public IdUtilImpl(DuracloudUserRepo userRepo,
                      DuracloudAccountRepo accountRepo,
                      DuracloudRightsRepo rightsRepo) {
        this.accountId = Collections.max(accountRepo.getIds());
        this.userId = Collections.max(userRepo.getIds());
        this.rightsId = Collections.max(rightsRepo.getIds());
    }

    @Override
    public int newAccountId() {
        return ++accountId;
    }

    @Override
    public int newUserId() {
        return ++userId;
    }

    @Override
    public int newRightsId() {
        return ++rightsId;
    }

}
