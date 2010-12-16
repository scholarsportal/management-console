/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import java.io.InputStream;

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

    public DuracloudImageRepo getImageRepo();

    public IdUtil getIdUtil();

}
