/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.util.IdUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
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
        this.accountId = max(accountRepo.getIds());
        this.userId = max(userRepo.getIds());
        this.rightsId = max(rightsRepo.getIds());
    }

    private int max(Collection<? extends Integer> c){
    	//this check is necessary because Collections.max(int)
    	//throws a NoSuchElementException when the collection 
    	//is empty.
    	return c.isEmpty() ? 0 : Collections.max(c);
    }
    @Override
    public synchronized int newAccountId() {
        return ++accountId;
    }

    @Override
    public synchronized int newUserId() {
        return ++userId;
    }

    @Override
    public synchronized int newRightsId() {
        return ++rightsId;
    }

}
