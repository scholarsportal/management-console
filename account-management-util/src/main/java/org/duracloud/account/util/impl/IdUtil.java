/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: Dec 3, 2010
 */
public class IdUtil {

    private static IdUtil idUtil = null;

    private int accountId = -1;
    private int userId = -1;
    private int rightsId = -1;


    private IdUtil() {
    }

    public static IdUtil instance() {
        if(null == idUtil) {
            idUtil = new IdUtil();
        }
        return idUtil;
    }

    public void initialize(DuracloudAccountRepo accountRepo,
                           DuracloudUserRepo userRepo,
                           DuracloudRightsRepo rightsRepo) {
        // TODO: Initialize local ID values to the highest ID found in each repo
    }

    private void checkInitialized() {
        if(accountId < 0 || userId < 0 || rightsId < 0) {
            throw new DuraCloudRuntimeException("IdUtil must be initialized");
        }
    }

    public int newAccountId() {
        return ++accountId;
    }

    public int newUserId() {
        return ++userId;
    }

    public int newRightsId() {
        return ++rightsId;
    }

}
