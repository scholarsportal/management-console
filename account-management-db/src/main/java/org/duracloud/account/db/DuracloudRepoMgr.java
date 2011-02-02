/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import java.io.InputStream;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Dec 9, 2010
 */
public interface DuracloudRepoMgr {

    public void initialize(InputStream xml);

    public DuracloudUserRepo getUserRepo();

    public DuracloudAccountRepo getAccountRepo();

    public DuracloudRightsRepo getRightsRepo();

    public DuracloudUserInvitationRepo getUserInvitationRepo();

    public DuracloudInstanceRepo getInstanceRepo();

    public DuracloudServerImageRepo getServerImageRepo();

    public DuracloudProviderAccountRepo getProviderAccountRepo();

    public DuracloudServiceRepositoryRepo getServiceRepositoryRepo();

    public IdUtil getIdUtil();

    public Set<BaseRepo> getAllRepos();

}
