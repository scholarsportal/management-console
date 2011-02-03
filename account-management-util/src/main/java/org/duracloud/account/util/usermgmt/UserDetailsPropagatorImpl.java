/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.usermgmt;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.security.domain.SecurityUserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Feb 2, 2011
 */
public class UserDetailsPropagatorImpl implements UserDetailsPropagator {

    private Logger log = LoggerFactory.getLogger(UserDetailsPropagatorImpl.class);

    private DuracloudRepoMgr repoMgr;

    public UserDetailsPropagatorImpl(DuracloudRepoMgr duracloudRepoMgr) {
        this.repoMgr = duracloudRepoMgr;
    }

    @Override
    public void propagateRights(int acctId, int userId, Set<Role> roles)
        throws DBNotFoundException {
    }

    @Override
    public void propagateRevocation(int acctId, int userId)
        throws DBNotFoundException {
    }
}
