/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util.impl;

import java.util.List;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.util.RootAccountManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class RootAccountManagerServiceImpl implements RootAccountManagerService {
	private Logger log = LoggerFactory.getLogger(getClass());
    private DuracloudUserRepo duracloudUserRepo;

    public RootAccountManagerServiceImpl(DuracloudUserRepo duracloudUserRepo) {
        this.duracloudUserRepo = duracloudUserRepo;
    }

	@Override
	public void addDuracloudImage(String imageId, String version,
			String description) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<AccountInfo> listAllAccounts(String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DuracloudUser> listAllUsers(String filter) {
		// TODO Auto-generated method stub
		return null;
	}

  
}
