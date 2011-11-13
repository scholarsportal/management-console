/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.account.init.domain.Initable;

import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Dec 9, 2010
 */
public interface DuracloudRepoMgr extends Initable {

    public void initialize(AmaConfig config);

    public DuracloudUserRepo getUserRepo();

    public DuracloudGroupRepo getGroupRepo();

    public DuracloudAccountRepo getAccountRepo();

    public DuracloudRightsRepo getRightsRepo();

    public DuracloudUserInvitationRepo getUserInvitationRepo();

    public DuracloudInstanceRepo getInstanceRepo();

    public DuracloudServerImageRepo getServerImageRepo();

    public DuracloudComputeProviderAccountRepo getComputeProviderAccountRepo();

    public DuracloudStorageProviderAccountRepo getStorageProviderAccountRepo();

    public DuracloudServiceRepositoryRepo getServiceRepositoryRepo();

    public IdUtil getIdUtil();

    public Set<BaseRepo> getAllRepos();

}
